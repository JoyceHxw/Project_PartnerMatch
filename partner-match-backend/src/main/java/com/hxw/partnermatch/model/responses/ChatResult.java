package com.hxw.partnermatch.model.responses;

import com.hxw.partnermatch.model.Team;
import com.hxw.partnermatch.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class ChatResult {
    /**
     * 最后发送消息的用户
     */
    private User user;
    /**
     * 最后消息
     */
    private String latestText;
    /**
     * 最后聊天时间
     */
    private Date latestTime;
    /**
     * 群聊队伍
     */
    private Team team;

    public ChatResult(User user, String latestText, Date latestTime, Team team){
        this.user=user;
        this.latestText=latestText;
        this.latestTime=latestTime;
        this.team=team;
    }
}
