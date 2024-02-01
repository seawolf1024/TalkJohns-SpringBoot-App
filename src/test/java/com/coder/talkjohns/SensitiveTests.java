package com.coder.talkjohns;

import com.coder.talkjohns.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TalkjohnsApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "There is a XXrabbitXX and a ratatatat Xgoose duck";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "There is a XX✰ra✰bbi✰✰✰t✰✰XX and a ra✰ta✰✰ta✰tat Xg✰✰oos✰e duck";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}


















