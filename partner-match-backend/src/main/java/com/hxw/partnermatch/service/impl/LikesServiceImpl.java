package com.hxw.partnermatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.mapper.LikesMapper;
import com.hxw.partnermatch.model.Blog;
import com.hxw.partnermatch.model.Likes;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.service.BlogService;
import com.hxw.partnermatch.service.LikesService;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Objects;


/**
* @author 81086
* @description 针对表【likes(点赞表)】的数据库操作Service实现
* @createDate 2023-09-28 00:01:29
*/
@Service
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes>
    implements LikesService {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private LikesMapper likesMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addLike(Likes likes, HttpServletRequest request) {
        if(likes==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        if(likes.getBlogId()==null || likes.getUserId()==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        //检验博文是否存在
        Blog blog = blogService.getById(likes.getBlogId());
        if(blog==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"博文不存在");
        }
        //检验用户
        User currentUser=userService.getCurrentUser(request);
        if(!Objects.equals(likes.getUserId(), currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有权限");
        }
        boolean save = this.save(likes);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"保存错误");
        }
        //博文表赞+1
        Integer likesCount = blog.getLikesCount();
        blog.setLikesCount(likesCount+1);
        boolean update = blogService.updateById(blog);
        if(!update){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"保存错误");
        }
        return Result.ok(save,"点赞成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancelLike(Likes likes, HttpServletRequest request) {
        if(likes==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Long blogId = likes.getBlogId();
        Long userId = likes.getUserId();
        if(blogId==null || userId==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        //检验是否使当前用户
        User currentUser=userService.getCurrentUser(request);
        if(!userId.equals(currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有权限");
        }
        //一个人只能给一个博文点一个赞
        //根据博文和用户id查找对应点赞是否存在
        LambdaQueryWrapper<Likes> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Likes::getBlogId,blogId).eq(Likes::getUserId,userId);
        Likes likes1 = likesMapper.selectOne(queryWrapper);
        if(likes1==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"没有点过赞，无法取消");
        }
        boolean remove = this.removeById(likes1);
        if(!remove){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"删除错误");
        }
        //博文表点赞数-1
        Blog blog = blogService.getById(blogId);
        Integer likesCount = blog.getLikesCount();
        blog.setLikesCount(likesCount-1);
        boolean update = blogService.updateById(blog);
        if(!update){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"更新错误");
        }
        return Result.ok(remove,"取消成功");
    }
}




