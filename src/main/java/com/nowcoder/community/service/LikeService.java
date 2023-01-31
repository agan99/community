package com.nowcoder.community.service;

public interface LikeService {

    /**
     * 给实体点赞 (第一次点赞，第二次取消点赞)
     * @param userId 用户id
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId 实体id
     */
    void likeEntity(int userId,int entityType, int entityId);

    /**
     * 查询实体点赞状态
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId 实体id
     * @param userId 用户id
     * @return 点赞状态
     */
    int findEntityLikeStatus(int entityType, int entityId, int userId);

    /**
     * 查询实体点赞数量
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId 实体id
     * @return 点赞数量
     */
    Long finEntityLikeCount(int entityType, int entityId);
}
