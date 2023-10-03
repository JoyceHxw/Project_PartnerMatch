package com.hxw.partnermatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxw.partnermatch.model.Chat;
import com.hxw.partnermatch.model.requests.ChatRequest;
import com.hxw.partnermatch.utils.Result;
import jakarta.servlet.http.HttpServletRequest;


/**
* @author 81086
* @description 针对表【chat(消息表)】的数据库操作Service
* @createDate 2023-09-23 20:21:01
*/
public interface ChatService extends IService<Chat> {

    /**
     * 获取聊天记录
     * @param chatRequest 对象id和消息类型
     * @param request 当前用户
     * @return
     */
    Result getChat(ChatRequest chatRequest, HttpServletRequest request);

    /**
     * 获取和当前用户有聊天记录的聊天对象
     * @param type 消息类型
     * @param request 当前用户
     * @return
     */
    Result getChatUserList(int type, HttpServletRequest request);

    /**
     * 获取当前用户所在的有群聊的队伍
     * @param type 消息类型
     * @param request 当前用户
     * @return
     */
    Result getChatTeamList(int type, HttpServletRequest request);

    /**
     * 保存数据在mysql，更新redis中
     * @param chat
     * @return
     */
    Result saveChat(Chat chat);
}
