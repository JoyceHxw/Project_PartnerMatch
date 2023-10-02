package com.hxw.partnermatch.controller;

import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Comments;
import com.hxw.partnermatch.model.requests.CommentsIdRequest;
import com.hxw.partnermatch.service.CommentsService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comment")
@CrossOrigin(origins = "http://127.0.0.1:5173",allowCredentials = "true") //解决跨域携带cookie的问题
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    @PostMapping("publish")
    public Result publishComment(@RequestBody Comments comments, HttpServletRequest request){
        if(comments==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=commentsService.publishComment(comments,request);
        return result;
    }

    @GetMapping("search")
    public Result searchComment(@RequestParam Long blogId){
        if(blogId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=commentsService.searchComment(blogId);
        return result;
    }

    @PostMapping("delete")
    public Result deleteComment(@RequestBody CommentsIdRequest commentsIdRequest, HttpServletRequest request){
        if(commentsIdRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=commentsService.deleteComment(commentsIdRequest,request);
        return result;
    }

    @GetMapping("count")
    public Result getCount(@RequestParam Long blogId){
        if(blogId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=commentsService.getCount(blogId);
        return result;
    }
}
