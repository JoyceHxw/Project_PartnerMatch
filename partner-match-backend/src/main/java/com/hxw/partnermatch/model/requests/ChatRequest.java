package com.hxw.partnermatch.model.requests;

import lombok.Getter;

@Getter
public class ChatRequest {
    /**
     * 消息对象id
     */
    private Long id;
    /**
     * 消息类型，0-私聊，1-群发
     */
    private int type;
}
