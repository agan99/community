package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.VO.FollowVO;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(entityType, entityId, user.getId());

        return CommunityUtil.getJSONString(0, "已关注!");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(entityType, entityId, user.getId());

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }

    @GetMapping("/followee/{userId}")
    public String getFollowee(@PathVariable("userId") int userId, Page page, Model model){
        User target = userService.findUserById(userId);
        if (target == null) {
            throw new RuntimeException("用户不存在!");
        }

        model.addAttribute("target", target);

        page.setLimit(5);
        page.setRows(Math.toIntExact(followService.getFolloweeCount(userId, ENTITY_TYPE_USER)));
        page.setPath("/followee/" + userId);

        List<FollowVO> followVOS = followService.listFollowee(hostHolder.getUser(), userId, page.getOffset(), page.getLimit());
        model.addAttribute("followeeList", followVOS);

        return "/site/followee";
    }

    @GetMapping("/follower/{userId}")
    public String getFollower(@PathVariable("userId") int userId, Page page, Model model){
        User target = userService.findUserById(userId);
        if (target == null) {
            throw new RuntimeException("用户不存在!");
        }

        model.addAttribute("target", target);

        page.setLimit(5);
        page.setRows(Math.toIntExact(followService.getFollowerCount(ENTITY_TYPE_USER, userId)));
        page.setPath("/follower/" + userId);

        List<FollowVO> followVOS = followService.listFollower(hostHolder.getUser(), userId, page.getOffset(), page.getLimit());
        model.addAttribute("followerList", followVOS);

        return "/site/follower";
    }

}