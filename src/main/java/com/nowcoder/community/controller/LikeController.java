package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;


    /**
     * 当前登录用户给实体点赞
     * @param entityType 实体类型 1-帖子 2-评论
     * @param entityId 实体id
     * @return
     */
    @ResponseBody
    @PostMapping("/like")
    public String like(int entityType, int entityId){
        User user = hostHolder.getUser();
        // 1. 当前用户给实体点赞
        likeService.likeEntity(user.getId() ,entityType, entityId);

        // 2. 查询点赞数量、点赞状态
        Long likeCount = likeService.finEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(entityType, entityId, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0,null, map);

    }
}
