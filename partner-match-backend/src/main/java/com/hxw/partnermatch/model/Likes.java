package com.hxw.partnermatch.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 点赞表
 * @TableName likes
 */
@TableName(value ="likes")
@Data
public class Likes implements Serializable {
    /**
     * 点赞id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 点赞的博文id
     */
    private Long blogId;

    /**
     * 点赞用户的id
     */
    private Long userId;

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