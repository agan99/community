package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.VO.FollowVO;

import java.util.List;

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
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    boolean hasFollowed(int userId, int entityType, int entityId);


    /**
     * 分页查询某个用户关注列表
     * @param loginUser
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return
     */
    List<FollowVO> listFollowee(User loginUser, int userId, int offset, int limit);

    /**
     * 分查询某个用户粉丝列表
     * @param loginUser
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return
     */
    List<FollowVO> listFollower(User loginUser, int userId, int offset, int limit) ;

}
