package com.nowcoder.community.service.impl;

import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisTemplate<String, Object>  redisTemplate;

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
}
