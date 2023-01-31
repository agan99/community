package com.nowcoder.community.service.impl;

import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 给实体点赞 (第一次点赞，第二次取消点赞)
     * like:entity:entityType:entityId -> set(userId)
     * @param userId 用户id
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId   实体id
     * @param entityUserId 该实体用户id
     */
    @Override
    public void likeEntity(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                // 统计该用户获得点赞次数
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                // 实现点赞功能
                // 判断该用户是否已经给本实体点赞
                Boolean member = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                redisOperations.multi();
                if (Boolean.TRUE.equals(member)) {
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }

    /**
     * 查询实体点赞状态
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId   实体id
     * @param userId     用户id
     * @return 点赞状态 1-点赞 0-未点赞
     */
    @Override
    public int findEntityLikeStatus(int entityType, int entityId, int userId) {
        // 1. 拼接 EntityKey
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 2. 查询数据库实体点赞状态
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(entityLikeKey, userId)) ? 1 : 0;
    }

    /**
     * 查询实体点赞数量
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId   实体id
     * @return 点赞数量
     */
    @Override
    public Long finEntityLikeCount(int entityType, int entityId) {
        // 1. 拼接 EntityKey
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 2. 查询redis实体点赞数量
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 获取某个用户被赞次数
     * @param entityUserId 实体用户id
     * @return 被赞次数
     */
    @Override
    public int findLikeUserCount(int entityUserId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
