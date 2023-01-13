package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 登录凭证信息表
 */
@Data
public class LoginTicket {

    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 登录凭证
     */
    private String ticket;

    /**
     * 凭证状态
     * 0-有效; 1-无效;
     */
    private Integer status;

    /**
     * 凭证失效时间
     */
    private Date expired;

}
