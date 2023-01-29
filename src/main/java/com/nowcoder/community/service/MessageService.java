package com.nowcoder.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;

import java.util.List;
import java.util.Map;

public interface MessageService extends IService<Message> {

    /**
     * 获取当前用户的所有私信
     * @param page 分页信息
     * @param user 当前登录用户
     * @return 私信列表信息
     */
    List<Map<String, Object>> getLetterList(Page page, User user);

    /**
     * 获取未读消息条数
     * @param toId 接收者
     * @param conversationId 会话id
     * @return 未读条数
     */
    Long findLetterUnreadCount(Integer toId, String conversationId);

    /**
     * 获取该会话总条数
     * @param conversationId 会话id
     * @return 会话总条数
     */
    int findLetterCount(String conversationId);

    /**
     * 统计当前用户的会话数量
     * @param userId
     * @return
     */
    int findConversationCount(int userId);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 获取会话详细信息
     * @param conversationId 会话id
     * @param page 翻页
     * @return
     */
    List<Map<String, Object>> getLetterDetail(String conversationId, Page page);

    /**
     * 私信目标
     * @param loginUser
     * @param conversationId
     * @return
     */
    User getLetterTarget(User loginUser, String conversationId);

    /**
     * 发送私信
     * @param toName 接收者
     * @param content 内容
     * @return Json提示信息
     */
    String sendLetter(User fromUser, String toName, String content);

    /**
     * 设置已读
     * @param letterDetails
     */
    void readMessage(User fromUser, List<Map<String, Object>> letterDetails);
}
