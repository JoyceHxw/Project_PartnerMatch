package com.hxw.partnermatch.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 博文表
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog implements Serializable {
    /**
     * 博文id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创作者id
     */
    private Long authorId;

    /**
     * 博文标题
     */
    private String title;

    /**
     * 博文内容
     */
    private String content;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 点赞数
     */
    private Integer likesCount;

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