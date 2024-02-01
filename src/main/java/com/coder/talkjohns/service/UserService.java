package com.coder.talkjohns.service;

import com.coder.talkjohns.dao.LoginTicketMapper;
import com.coder.talkjohns.dao.UserMapper;
import com.coder.talkjohns.entity.LoginTicket;
import com.coder.talkjohns.entity.User;
import com.coder.talkjohns.utils.CommunityConstant;
import com.coder.talkjohns.utils.CommunityUtil;
import com.coder.talkjohns.utils.MailClient;
import com.coder.talkjohns.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    RedisTemplate redisTemplate;

    public User findUserById(int id){
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        // empty case
        if(user == null){
            throw new IllegalArgumentException("The parameter should not be empty!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "Empty username.");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "Empty password field.");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "Empty email field.");
            return map;
        }

        // verify account
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "This username already exists.");
            return map;
        }
        // verify email account
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "This email account already exists.");
            return map;
        }

        //register
        //Database: user(id, username, password, salt, email, type, status, activation_code, header_url, create_time)
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5((user.getPassword() + user.getSalt())));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // send activation email
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        //templates/mail/activation.html

        try {  mailClient.sendMail(user.getEmail(), "TalkJohns-Activate your account.", content);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("emailMsg", "Server failed to send email to this account.");
            return map;

        }
        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        // empty value
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "Empty username.");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "Empty password.");
            return map;
        }

        // verify username
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "This account does not exist.");
            return map;
        }

        // verify status
        if(user.getStatus() == 0){
            map.put("usernameMsg", "This account has not been verified.");
            return map;
        }

        // verify password
        System.out.println("password = " + password);
        password = CommunityUtil.md5(password + user.getSalt());
        System.out.println("salt = " + user.getSalt());
        System.out.println("password2 = " + password);
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "Incorrect password.");
            return map;
        }

        // generate login ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }


    public LoginTicket findLoginTicket(String ticket){
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl){
//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    // 1. prefer to get User from cache
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    // 2. If failed to get User from cache, init cache data
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 3. When data changes, clear cache
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}





















