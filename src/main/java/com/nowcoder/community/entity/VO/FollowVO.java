package com.nowcoder.community.entity.VO;

import com.nowcoder.community.entity.User;
import lombok.Data;

import java.util.Date;

/**
 * 展示粉丝列表和关注列表用
 */
@Data
public class FollowVO {

    /**
     * 用户信息
     */
    private User user;

    /**
     * 关注时间
     */
    private Date followTime;
    /**
     * 当前登录用户是否关注
     */
    private boolean isFollowed;

}
