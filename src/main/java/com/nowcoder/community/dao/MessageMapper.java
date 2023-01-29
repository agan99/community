package com.nowcoder.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nowcoder.community.entity.Message;

import java.util.List;

/**
 * 私信列表 mapper 层
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询当前用户的会话列表,针对每个会话只返回一条最新的私信
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(Integer userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 根据会话id分页查询会话信息
     * @param conversationId 会话id
     * @param offset
     * @param limit
     * @return 消息列表
     */
    List<Message> selectListByConversationId(String conversationId, int offset, int limit);

    /**
     * 修改消息状态
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids, int status);
}
