package com.coder.talkjohns.controller;

import com.coder.talkjohns.entity.DiscussPost;
import com.coder.talkjohns.entity.Page;
import com.coder.talkjohns.entity.User;
import com.coder.talkjohns.service.DiscussPostService;
import com.coder.talkjohns.service.LikeService;
import com.coder.talkjohns.service.UserService;
import com.coder.talkjohns.utils.CommunityConstant;
import com.coder.talkjohns.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){

        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post: list){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        //discussPosts = {{"post", DiscussPost, "user": User}, [...}, {...}}
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page",page);
        return "/index"; // templates/index(.html)
    }

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public String getSchool1(Model model){
        model.addAttribute("name", "USC");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    @RequestMapping(path = "/test2", method = RequestMethod.GET)
    @ResponseBody
    public String getSchool2(){
        return "abcdef";
    }

    @RequestMapping(path = "error", method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}















