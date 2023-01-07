package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private Integer userType;
    private Integer userStatus;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
