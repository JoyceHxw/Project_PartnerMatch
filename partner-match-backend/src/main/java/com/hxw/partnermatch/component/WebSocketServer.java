package com.hxw.partnermatch.component;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Chat;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.Message;
import com.hxw.partnermatch.model.responses.ChatUserResult;
import com.hxw.partnermatch.service.ChatService;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 类似于controller，但实现的是双通道的websocket服务
 */

@ServerEndpoint("/chat/{id}/{type}") //将一个普通的 Java 类转化为 WebSocket 服务器端点，从而允许通过 WebSocket 连接与客户端进行通信
@Component
public class WebSocketServer {
    /**
     * 存所有的连接服务的客户端，线程安全
     * 不同的对象建立不同的session保存到sessionMap中，从而可以互相发消息
     */
    //用id+type拼接而成的string作为键，区分私聊和群聊
    public static final Map<String, Session> sessionMap=new ConcurrentHashMap<>();

    //不能自动注入，spring默认管理的都是单例（singleton），和 websocket （多对象）相冲突。
    private static ChatService chatService;

    private static UserService userService;

    @Autowired
    public void setChatService(ChatService chatService){
        WebSocketServer.chatService=chatService;
    }

    @Autowired
    public void setUserService(UserService userService){
        WebSocketServer.userService=userService;
    }

    /**
     * 连接建立成功调用的方法
     * @param id 用户id
     * @param session 会话
     */
    @OnOpen
    public void onOpen(@PathParam("id") Long id, @PathParam("type") Integer type, Session session){
        String key=getKey(id,type);
        sessionMap.put(key,session);
//        sendAllMessage(id,"连接成功"); //服务器给所有客户端发送消息
    }

    /**
     * 连接关闭调用的方法
     * @param id 用户id
     */
    @OnClose
    public void onClose(@PathParam("id") Long id, @PathParam("type") Integer type ){
        String key=getKey(id,type);
        sessionMap.remove(key);
    }

    /**
     * 服务器收到客户端消息后调用的方法
     * @param message json格式chat对象
     */
    @OnMessage
    public void onMessage(String message){
        if(message==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Gson gson = new Gson();
        Chat chat = gson.fromJson(message, Chat.class);
        Long toId = chat.getToId(); //发送对象id
        Long fromId = chat.getFromId(); //发送对象id
        Integer type=chat.getType();
        String content = chat.getContent(); //发送内容
        if(toId==null || fromId==null || content==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //把接收到的消息进行封装，带上用户信息，再转发出去
        ChatUserResult chatUserResult;
        User fromUser=userService.getById(chat.getFromId());
        if(type==0){
            User toUser=userService.getById(chat.getToId());
            chatUserResult=new ChatUserResult(
                    chat.getToId(),toUser.getUsername(),toUser.getAvatar(),
                    chat.getFromId(),fromUser.getUsername(),fromUser.getAvatar(),
                    chat.getContent(),chat.getCreateTime()
            );
        }
        else{
            chatUserResult=new ChatUserResult(
                    chat.getToId(),null,null,
                    chat.getFromId(),fromUser.getUsername(),fromUser.getAvatar(),
                    chat.getContent(),chat.getCreateTime()
            );
        }
        String messageAfter=gson.toJson(chatUserResult);
        send(toId,type,messageAfter);
        save(chat);
    }

    @OnError
    public void onError(Throwable error){
//        throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"WebSocket错误");
        error.printStackTrace();
    }

    /**
     * 发送给客户端的消息
     * @param id 对象id
     * @param message 消息对象
     */
    private void send(Long id,Integer type,String message){
        String key=getKey(id,type);
        if(message==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"不能传递空白信息");
        }
        Session toSession=sessionMap.get(key);
        //允许发送离线消息
        if(toSession==null){
            return;
        }
        //异步发送
        synchronized(toSession){
            toSession.getAsyncRemote().sendText(message);
        }
    }

    private void save(Chat chat){
        boolean result = chatService.save(chat);
        if(!result){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"消息保存错误");
        }
    }

    private String getKey(Long id, Integer type){
        return Long.toString(id)+Integer.toString(type);
    }

    /**
     * 群发
     * @param message 消息文本
     */
//    private void sendToAll(String message){
//        if(message==null){
//            throw new BusinessException(ResultCodeEnum.IS_NULL,"消息参数为空");
//        }
//        for(Session session: sessionMap.values()){
//            session.getAsyncRemote().sendText(message);
//        }
//    }

}
