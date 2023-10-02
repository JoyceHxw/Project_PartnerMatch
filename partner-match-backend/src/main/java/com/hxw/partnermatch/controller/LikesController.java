package com.hxw.partnermatch.controller;

import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Likes;
import com.hxw.partnermatch.service.LikesService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("like")
@CrossOrigin(origins = "http://127.0.0.1:5173",allowCredentials = "true") //解决跨域携带cookie的问题
public class LikesController {
    @Autowired
    private LikesService likesService;

    @PostMapping("add")
    public Result addLike(@RequestBody Likes likes, HttpServletRequest request){
        if(likes==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=likesService.addLike(likes,request);
        return result;
    }

    @PostMapping("cancel")
    public Result cancelLike(@RequestBody Likes likes,HttpServletRequest request){
        if(likes==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=likesService.cancelLike(likes,request);
        return result;
    }
}
