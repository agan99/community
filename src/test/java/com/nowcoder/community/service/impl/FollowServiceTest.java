package com.nowcoder.community.service.impl;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.VO.FollowVO;
import com.nowcoder.community.service.FollowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class FollowServiceTest {

    @Autowired
    FollowService followService;

    @Test
    public void listFolloweeTest(){
        User user = new User();
        user.setId(154);
        List<FollowVO> followVOS = followService.listFollowee(user, 154, 0, 5);
        for (FollowVO followVO : followVOS) {
            System.out.println(followVO.getUser().getId());
            System.out.println(followVO.getFollowTime());
            System.out.println(followVO.isFollowed());
            System.out.println("---------------------------------");
        }
    }

    @Test
    public void listFollowerTest(){
        User user = new User();
        user.setId(154);
        List<FollowVO> followVOS = followService.listFollower(user, 154, 0, 5);
        for (FollowVO followVO : followVOS) {
            System.out.println(followVO.getUser().getId());
            System.out.println(followVO.getFollowTime());
            System.out.println(followVO.isFollowed());
            System.out.println("---------------------------------");
        }
    }
}
