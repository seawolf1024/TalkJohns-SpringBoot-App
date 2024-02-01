package com.coder.talkjohns;

import com.coder.talkjohns.entity.User;
import com.coder.talkjohns.utils.CommunityUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.coder.talkjohns.utils.CommunityUtil.generateUUID;
import static com.coder.talkjohns.utils.CommunityUtil.md5;

public class GrammarTests {
    @Test
    public void HashMapTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("post", 1);
        map.put("user", 2);
        System.out.println(map);
    }

    @Test
    public void generateUUIDTest(){
        String uuid = generateUUID();
        System.out.println(uuid);
    }

    @Test
    public void md5Test(){
        String key = "abc";
        System.out.println(md5(key));
    }

    @Test
    public void jsonTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", 10);
        map.put("likeStatus", true);

        System.out.println(CommunityUtil.getJSONString(0, null, map));
        System.out.println(CommunityUtil.getJSONString(0, "Unfollowed"));
    }
}
