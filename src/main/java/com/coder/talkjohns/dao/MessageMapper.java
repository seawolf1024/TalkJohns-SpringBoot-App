package com.coder.talkjohns.dao;

import com.coder.talkjohns.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // select current user's whole conversation list
    List<Message> selectConversations(int userId, int offset, int limit);

    // select current user's conversation count
    int selectConversationCount(int userId);

    // select user's specific message list with somebody
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // select user's specific message list count
    int selectLetterCount(String conversationId);

    // select unread message count
    int selectLetterUnreadCount(int userId, String conversationId);

    // insert message
    int insertMessage(Message message);

    // edit message's status
    int updateStatus(List<Integer> ids, int status);
}

























