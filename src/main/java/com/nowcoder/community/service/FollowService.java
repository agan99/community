package com.nowcoder.community.service;

public interface FollowService {

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @param userId
     */
    void follow(int entityType, int entityId,int userId);

    /**
     * 取关
     * @param entityType
     * @param entityId
     * @param userId
     */
    void unfollow(int entityType, int entityId,int userId);

    /**
     * 显示关注数
     * @param userId
     * @param entityType
     * @return
     */
    Long getFolloweeCount(int userId, int entityType);

    /**
     * 显示粉丝数
     * @param entityType
     * @param entityId
     * @return
     */
    Long getFollowerCount(int entityType, int entityId);

    /**
     * 查询某个用户是否已关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean hasFollowed(int userId, int entityType, int entityId);
}
