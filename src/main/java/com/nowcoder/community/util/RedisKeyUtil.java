package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_USER_TOKEN = "user-token";


    /**
     * 拼接 EntityKey
     * like:entity:entityType:entityId -> set(userId)
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 改用户获得点赞次数
     * like:user → int
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个实体有多少用户关注
     * follower:entityType:entityId -> (userId, now)
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户关注了多少个实体
     * followee:userId:entityType → (entityId, now)
     * @param userId 用户id
     * @param entityType 实体类型
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * kaptcha:uuid → 验证码
     * @param uuid
     * @return
     */
    public static String getKapthaKey(String uuid){
        return PREFIX_KAPTCHA + SPLIT + uuid;
    }

    /**
     * ticket:ticket → LoginTicket
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * user-token:token → (userId)
     * @param token
     * @return
     */
    public static String getUserTokenKey(String token){
        return PREFIX_USER_TOKEN + SPLIT + token;
    }
    /**
     * user:userid → user
     * @param userId
     * @return
     */
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}
