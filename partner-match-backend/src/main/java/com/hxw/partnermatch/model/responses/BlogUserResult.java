package com.hxw.partnermatch.model.responses;

import com.hxw.partnermatch.model.Blog;
import com.hxw.partnermatch.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class BlogUserResult {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String username;
    private String avatar;
    private Date createTime;
    private Integer commentsCount;
    private Integer likesCount;
    /**
     * 当前用户是否点在这篇博文
     */
    private Boolean isLikedByCurrentUser;

    public BlogUserResult(Blog blog, User author){
        this.id=blog.getId();
        this.title=blog.getTitle();
        this.content=blog.getContent();
        this.authorId=author.getId();
        this.username=author.getUsername();
        this.avatar=author.getAvatar();
        this.createTime=blog.getCreateTime();
        this.commentsCount=blog.getCommentsCount();
        this.likesCount=blog.getLikesCount();
    }
}
