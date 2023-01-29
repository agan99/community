package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 私信表
 */
@Data
public class Message {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发送人
     */
    private Integer fromId;

    /**
     * 接收人
     */
    private Integer toId;

    /**
     * 冗余
     * 发送人_接收人
     */
    private String conversationId;

    /**
     * 消息
     */
    private String content;

    /**
     * 0-未读;1-已读;2-删除;
     * 消息状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}
