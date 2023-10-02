package com.hxw.partnermatch.model.responses;

import lombok.Data;

import java.util.Date;

@Data
public class ChatUserResult {
    private Long toId;
    private String toUsername;
    private String toAvatar;
    private Long fromId;
    private String fromUsername;
    private String fromAvatar;
    private String content;
    private Date createTime;

    public ChatUserResult(Long toUserId, String toUserName, String toAvatar, Long fromUserId, String fromUserName, String fromAvatar, String content, Date createTime) {
        this.toId = toUserId;
        this.toUsername = toUserName;
        this.toAvatar = toAvatar;
        this.fromId = fromUserId;
        this.fromUsername = fromUserName;
        this.fromAvatar = fromAvatar;
        this.content = content;
        this.createTime = createTime;
    }
}
