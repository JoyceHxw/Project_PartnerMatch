package com.hxw.partnermatch.model.responses;

import com.hxw.partnermatch.model.Comments;
import com.hxw.partnermatch.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class CommentUserResult {
    private Long id;
    private Long blogId;
    private Long commenterId;
    private String commentContent;
    private Date createTime;
    private String commenterUsername;
    private String commenterAvatar;

    public CommentUserResult(Comments comments, User commenter){
        this.id=comments.getId();
        this.blogId=comments.getBlogId();
        this.commenterId=comments.getCommenterId();
        this.commentContent=comments.getCommentContent();
        this.createTime=comments.getCreateTime();
        this.commenterUsername=commenter.getUsername();
        this.commenterAvatar=commenter.getAvatar();
    }
}
