package com.nowcoder.community.service.impl;
import lombok.Data;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.VO.FollowVO;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService, CommunityConstant {

    @Autowired
    private RedisTemplate<String, Object>  redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 关注
     *
     * @param entityType
     * @param entityId
     * @param userId
     */
    @Override
    public void follow(int entityType, int entityId, int userId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // follower:entityType:entityId -> (userId, now)
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // followee:userId:entityType → (entityId, now)
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                redisOperations.multi();
                // 给指定实体添加关注用户（粉丝数）
                redisTemplate.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                // 给指定用户添加实体（关注数）
                redisTemplate.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    /**
     * 取关
     *
     * @param entityType
     * @param entityId
     * @param userId
     */
    @Override
    public void unfollow(int entityType, int entityId, int userId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // follower:entityType:entityId -> (userId, now)
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // followee:userId:entityType → (entityId, now)
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                redisOperations.multi();

                redisTemplate.opsForZSet().remove(followerKey, userId, System.currentTimeMillis());
                redisTemplate.opsForZSet().remove(followeeKey, entityId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    /**
     * 显示关注数
     * @param userId
     * @param entityType
     * @return
     */
    @Override
    public Long getFolloweeCount(int userId, int entityType) {
        // followee:userId:entityType → (entityId, now)
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 显示粉丝数
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询某个用户是否已关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * 分页查询某个用户关注列表
     * @param loginUser
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<FollowVO> listFollowee(User loginUser, int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        // 按关注时间倒叙 显示关注用户Id
        Set<Object> userIdList =  redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (userIdList == null){
            return null;
        }
        return listFollows(loginUser, followeeKey, userIdList);
    }


    /**
     * 分查询某个用户粉丝列表
     * @param loginUser
     * @param userId 用户id
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<FollowVO> listFollower(User loginUser, int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        // 按关注时间倒叙 显示关注用户Id
        Set<Object> userIdList =  redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (userIdList == null){
            return null;
        }
        return listFollows(loginUser, followerKey, userIdList);
    }

    /**
     * 封装粉丝列表和关注列表数据
     * @param redisKeys
     * @param userIdList
     * @param loginUser
     * @return
     */
    public List<FollowVO> listFollows(User loginUser, String redisKeys, Set<Object> userIdList){
        // 构建粉丝和关注列表
        List<FollowVO> followVOS = new ArrayList<>();
        boolean hasFollowed = loginUser != null;
        for (Object id : userIdList) {
            FollowVO followVO = new FollowVO();
            User user = userService.findUserById((int) id);

            followVO.setUser(user);
            // 关注时间
            Double score = redisTemplate.opsForZSet().score(redisKeys, id);
            followVO.setFollowTime(new Date(score.longValue()));
            // 当前登录用户是否关注
            if (hasFollowed) {
                hasFollowed = hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, user.getId());
                followVO.setFollowed(hasFollowed);
            }
            followVOS.add(followVO);
        }
        return followVOS;
    }
}
