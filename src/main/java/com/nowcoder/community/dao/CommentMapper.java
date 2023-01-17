package com.nowcoder.community.dao;


import com.nowcoder.community.entity.Comment;

import java.util.List;

public interface CommentMapper {

    /**
     * 查询评论
     * @param entityType 1-帖子 2-评论
     * @param entityId 帖子id
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 统计查询数量
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);
}
