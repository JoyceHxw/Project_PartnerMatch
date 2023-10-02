package com.hxw.partnermatch.controller;

import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Chat;
import com.hxw.partnermatch.model.requests.ChatRequest;
import com.hxw.partnermatch.service.ChatService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;

@RestController
@RequestMapping("chat")
@CrossOrigin(origins = "http://127.0.0.1:5173",allowCredentials = "true") //解决跨域携带cookie的问题
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("getChat")
    public Result getPrivateChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request){
        if(chatRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result= chatService.getChat(chatRequest,request);
        return result;
    }

    @GetMapping("getChatUserList")
    public Result getChatUserList(@RequestParam int type, HttpServletRequest request){
        if(type<0 || type>1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        Result result=chatService.getChatUserList(type,request);
        return result;
    }

    @GetMapping("getChatTeamList")
    public Result getChatTeamList(@RequestParam int type, HttpServletRequest request){
        if(type<0 || type>1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        Result result=chatService.getChatTeamList(type,request);
        return result;
    }
}
