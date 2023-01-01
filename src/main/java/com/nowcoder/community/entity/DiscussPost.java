package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DiscussPost {
    private Integer id;
    // 用户id
    private Integer userId;
    // 帖子标题
    private String title;
    // 帖子内容
    private String content;
    /**
     * 帖子状态
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer discussStatus;

    /**
     * 帖子类型
     * 0-普通; 1-置顶;
     */
    private Integer discussType;
    // 帖子发布时间
    private Date createTime;
    // 帖子评论数量
    private Integer commentCount;
    // 帖子分数
    private Double score;

}
