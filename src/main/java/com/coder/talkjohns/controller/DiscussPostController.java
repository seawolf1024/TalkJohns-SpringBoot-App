package com.coder.talkjohns.controller;

import com.coder.talkjohns.entity.Comment;
import com.coder.talkjohns.entity.DiscussPost;
import com.coder.talkjohns.entity.Page;
import com.coder.talkjohns.entity.User;
import com.coder.talkjohns.service.CommentService;
import com.coder.talkjohns.service.DiscussPostService;
import com.coder.talkjohns.service.LikeService;
import com.coder.talkjohns.service.UserService;
import com.coder.talkjohns.utils.CommunityConstant;
import com.coder.talkjohns.utils.CommunityUtil;
import com.coder.talkjohns.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403, "Please login to send posts.");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // exceptions will be handled in the future
        return CommunityUtil.getJSONString(0, "Posted successfully.");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDisscussPost(@PathVariable int discussPostId, Model model, Page page){
        // post
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // user
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //likeCount
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // like status
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // page info
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // comment: post's comment
        // reply: comment's comment

        // commentList = {comment1, comment2, ...}
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // commentVoList = {commentVo1, commentVo2, ...}
        // = {commentVo1 = {"comment":..., "user":..., "replys"={"reply":..., "user":..., "target":...}, "replyCount":...}
        //    commentVo2 = {"comment":..., "user":..., "replys"={"reply":..., "user":..., "target":...}, "replyCount":...}
        //    commentVo3 = {"comment":..., "user":..., "replys"={"reply":..., "user":..., "target":...}, "replyCount":...}
        //    }
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment: commentList){
                // commentVo = {"comment":..., "user":..., "replys":..., "replyCount":...}
                Map<String, Object> commentVo = new HashMap<>();
                // comment
                commentVo.put("comment", comment);
                // user
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //likeCount
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // like status
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                // replyList = {reply1, reply2, ...}
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // replyVoList = {replyVo1, replyVo2, ...}
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyVoList != null){
                    for(Comment reply: replyList){
                        // replyVo = {"reply":..., "user":..., "target":...}
                        Map<String, Object> replyVo = new HashMap<>();
                        // reply
                        replyVo.put("reply", reply);
                        // user
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // target
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        //likeCount
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // like status
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // numbers of replys
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

}















