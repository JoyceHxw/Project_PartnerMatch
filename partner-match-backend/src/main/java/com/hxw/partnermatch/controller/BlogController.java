package com.hxw.partnermatch.controller;

import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Blog;
import com.hxw.partnermatch.model.requests.BlogIdRequest;
import com.hxw.partnermatch.model.requests.BlogSearchRequest;
import com.hxw.partnermatch.service.BlogService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;

@RestController
@RequestMapping("blog")
@CrossOrigin(origins = "http://127.0.0.1:5173",allowCredentials = "true") //解决跨域携带cookie的问题
public class BlogController {
    @Autowired
    private BlogService blogService;

    @PostMapping("publish")
    public Result publishBlog(@RequestBody Blog blog, HttpServletRequest request){
        if(blog==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=blogService.publishBlog(blog,request);
        return result;
    }

    @PostMapping("search")
    public Result searchBlog(@RequestBody(required = false)BlogSearchRequest blogSearchRequest, HttpServletRequest request){
        Result result=blogService.searchBlog(blogSearchRequest,request);
        return result;
    }

    @PostMapping("update")
    public Result updateBlog(@RequestBody Blog blog,HttpServletRequest request){
        Result result=blogService.updateBlog(blog,request);
        return result;
    }

    @PostMapping("delete")
    public Result deleteBlog(@RequestBody BlogIdRequest blogIdRequest, HttpServletRequest request){
        if(blogIdRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=blogService.deleteBlog(blogIdRequest,request);
        return result;
    }

    @GetMapping("getById")
    public Result getBlogById(@RequestParam Long id,HttpServletRequest request){
        if(id==null || id<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍参数错误");
        }
        Result result=blogService.getBlogById(id,request);
        return result;
    }
}
