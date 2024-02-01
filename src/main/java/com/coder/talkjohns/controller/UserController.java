package com.coder.talkjohns.controller;

import com.coder.talkjohns.annotation.LoginRequired;
import com.coder.talkjohns.entity.User;
import com.coder.talkjohns.service.FollowService;
import com.coder.talkjohns.service.LikeService;
import com.coder.talkjohns.service.UserService;
import com.coder.talkjohns.utils.CommunityConstant;
import com.coder.talkjohns.utils.CommunityUtil;
import com.coder.talkjohns.utils.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error", "Please select an image.");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename(); // xxx.png
        String suffix = fileName.substring(fileName.lastIndexOf(".")); //.png
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "Invalid image format.");
            return "/site/setting";
        }

        // generate random image name
        fileName = CommunityUtil.generateUUID()  + suffix;
        // decide the path to store the image
        File dest = new File(uploadPath + "/" + fileName); // e:/work/data/upload/XXX.png

        try {
            // save the image
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("Failed to upload the image.", e.getMessage());
            throw new RuntimeException("Failed to upload the image. Server exception occurred.", e);
        }

        // update the web path
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // disk path for storing the image
        fileName = uploadPath + "/" + fileName;
        // suffix
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // response
        response.setContentType("image/" + suffix);

        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("Failed to read the image." + e.getMessage());
        }
    }

    // user profile
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("Invaild user");
        }

        // user
        model.addAttribute("user", user);
        // likeCount
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // followee count
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // follower count
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // if followed
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
            System.out.println("hasFollowed = " + hasFollowed);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}


















