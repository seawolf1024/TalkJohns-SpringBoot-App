package com.coder.talkjohns.service;

import com.coder.talkjohns.dao.DiscussPostMapper;
import com.coder.talkjohns.dao.UserMapper;
import com.coder.talkjohns.entity.DiscussPost;
import com.coder.talkjohns.entity.User;
import com.coder.talkjohns.utils.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
//@Scope("prototype")
public class AlphaService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    TransactionTemplate transactionTemplate;

    //Initialization
    public AlphaService(){
        System.out.println("Instantiating alphaservice...");
    }

    @PostConstruct
    public void init(){
        System.out.println("Initializing alphaservice...");
    }

    @PreDestroy
    public void destory(){
        System.out.println("Destroying alphaservice...");
    }

    // REQUIRED: support current transaction(outer transaction). If there's no such transaction,
    //      it will create new transaction.
    // REQUIRES_NEW: create a new transaction and pause current transaction
    // NESTED: If there exists current transaction(outer transaction), then the inner transaction
    //      is executed inner this transasction(independent commit and rollback), or it will be
    //      the same case with REQUIRED.
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        // create user
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // create post
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("New user!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // create user
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/199t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // create post
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hi");
                post.setContent("Neo user!");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");

                return "ok";
            }
        });
    }

}























