package com.nowcoder.community.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * 私信列表 service 层
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService, CommunityConstant {


    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 获取当前用户的所有私信
     * @param page 分页信息
     * @param user 当前登录用户
     * @return 私信列表信息
     */
    public List<Map<String, Object>> getLetterList(Page page, User user){
        List<Map<String, Object>> list = new ArrayList<>();

        // 1. 获取当前用户的会话列表
        // 会话列表
        List<Message> conversationList = messageMapper.selectConversations(user.getId(),
                page.getOffset(), page.getLimit());

        // 2. 封装数据
        for (Message message : conversationList) {
            Map<String, Object> map = new HashMap<>();
            // 未读私信条数
            map.put("unreadCount", this.findLetterUnreadCount(user.getId(), message.getConversationId()));
            // 总条数
            map.put("letterCount", this.findLetterCount(message.getConversationId()));
            // 最新消息
            map.put("content", message.getContent());
            // 发送时间
            map.put("createTime", message.getCreateTime());
            // 会话id
            map.put("conversationId", message.getConversationId());

            // 发送用户
            int targetId = Objects.equals(user.getId(), message.getFromId()) ? message.getToId():message.getFromId();
            // 用户信息去敏感
            User safetyUser = getSafetyUser(userMapper.selectById(targetId));
            map.put("target", safetyUser);

            list.add(map);
        }
        return list;
    }

    /**
     * 获取会话详细信息
     * @param conversationId 会话id
     * @param page 翻页
     * @return
     */
    public List<Map<String, Object>> getLetterDetail(String conversationId, Page page){
        List<Map<String, Object>> list = new ArrayList<>();

        // 1.获取私信详情消息
        // 私信信息
        List<Message> messages = messageMapper.selectListByConversationId(
                conversationId, page.getOffset(), page.getLimit());
        if (messages == null) {
            return null;
        }
        // 拆分用户id, 获取用户数据
        String[] ids = conversationId.split("_");
        // 用户脱敏
        User safetyUser1 = getSafetyUser(userMapper.selectById(ids[0]));
        User safetyUser2 = getSafetyUser(userMapper.selectById(ids[1]));

        // 2.封装数据
        for (Message message : messages) {
            Map<String, Object> map = new HashMap<>();
            // 私信id
            map.put("id", message.getId());
            // 消息
            map.put("content", message.getContent());
            // 私信时间
            map.put("createTime", message.getCreateTime());
            // 会话id
            map.put("conversationId", message.getConversationId());
            // 消息状态
            map.put("status", message.getStatus());
            // 消息发送者
            map.put("fromUser", Objects.equals(message.getFromId(), safetyUser1.getId())
                    ? safetyUser1 : safetyUser2);

            list.add(map);
        }

        return list;
    }

    /**
     * 私信目标
     * @return
     */
    public User getLetterTarget(User loginUser, String conversationId){
        String[] ids = conversationId.split("_");

        if (loginUser.getId() == Integer.parseInt(ids[0])) {
            return getSafetyUser(userMapper.selectById(ids[1]));
        } else {
            return getSafetyUser(userMapper.selectById(ids[0]));
        }
    }

    /**
     * 发送私信
     * @param toName 接收者
     * @param content 内容
     * @return Json提示信息
     */
    @Override
    public String sendLetter(User fromUser, String toName, String content) {
        // 1.检验数据
        // 判断是否发送给自己
        if (fromUser.getUsername().equals(toName)){
            return CommunityUtil.getJSONString(1, "不能私信给自己!");
        }

        // 查找是否有目标用户
        User toUser = userMapper.selectByName(toName);
        if (toUser == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }

        // 2.保存数据
        // 设置私信表数值
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());

        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        // 处理私信内容
        message.setContent(HtmlUtils.htmlEscape(content));
        message.setContent(sensitiveFilter.filter(content));

        message.setStatus(0);
        message.setCreateTime(new Date());

        messageMapper.insert(message);

        return CommunityUtil.getJSONString(0);
    }


    /**
     * 设置消息为已读
     * @param fromUser
     * @param letterDetails
     */
    @Override
    public void readMessage(User fromUser, List<Map<String, Object>> letterDetails) {
        List<Integer> ids = new ArrayList<>();

        if (letterDetails != null) {
            for (Map<String, Object> detail : letterDetails) {
                User user = (User) detail.get("fromUser");
                // 取出接收者私信和私信状态为未读的私信id
                if (user.getId() != fromUser.getId() && (Integer) detail.get("status") == 0){
                    ids.add((Integer) detail.get("id"));
                }
            }
        }
        // 修改消息为已读
        if (ids != null && !ids.isEmpty()) {
            messageMapper.updateStatus(ids, 1);
        }
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    public User getSafetyUser(User user){
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setHeaderUrl(user.getHeaderUrl());
        return safetyUser;
    }

    /**
     * 获取该会话总条数
     * @param conversationId 会话id
     * @return 会话总条数
     */
    public int findLetterCount(String conversationId){
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        qw.eq(Message::getConversationId, conversationId);
        // 排除删除状态的消息
        qw.ne(Message::getStatus, MESSAGE_STATUS_DELETE);
        return Math.toIntExact(messageMapper.selectCount(qw));
    }

    /**
     * 获取未读消息条数
     * @param toId 接收者
     * @param conversationId 会话id
     * @return
     */
    public Long findLetterUnreadCount(Integer toId, String conversationId){
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        // 消息未读状态
        qw.eq(Message::getStatus, MESSAGE_STATUS_Unread);
        // 排除系统用户
        qw.ne(Message::getFromId, SYSTEM_USERID);
        // 接收用户
        qw.eq(Message::getToId, toId);
        // 会话id
        if (conversationId != null) {
            qw.eq(Message::getConversationId, conversationId);
        }
        return messageMapper.selectCount(qw);
    }

    /**
     * 统计当前用户的会话数量
     * @param userId
     * @return
     */
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
}
