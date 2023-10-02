package com.hxw.partnermatch.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息表
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat implements Serializable {
    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息发送方id
     */
    private Long fromId;

    /**
     * 消息接收方id
     */
    private Long toId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型，0-私聊，1-群发
     */
    private Integer type;

    /**
     * 消息发送状态，0-已发送，1-发送失败，2-撤回
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 是否逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}