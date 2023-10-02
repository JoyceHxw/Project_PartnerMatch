package com.hxw.partnermatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.mapper.CommentsMapper;
import com.hxw.partnermatch.model.Blog;
import com.hxw.partnermatch.model.Comments;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.CommentsIdRequest;
import com.hxw.partnermatch.model.responses.CommentUserResult;
import com.hxw.partnermatch.service.BlogService;
import com.hxw.partnermatch.service.CommentsService;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
* @author 81086
* @description 针对表【comments(评论表)】的数据库操作Service实现
* @createDate 2023-09-27 18:24:40
*/
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
    implements CommentsService {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentsMapper commentsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result publishComment(Comments comments, HttpServletRequest request) {
        if(comments==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Long blogId = comments.getBlogId();
        if(blogId==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        //检验博文是否存在
        Blog blog = blogService.getById(blogId);
        if(blog==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"博文不存在");
        }
        //检验用户
        User currentUser=userService.getCurrentUser(request);
        if(!Objects.equals(comments.getCommenterId(), currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"不是当前用户");
        }
        //检验内容是否为空
        if(comments.getCommentContent()==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"评论内容为空");
        }
        boolean save = this.save(comments);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"保存错误");
        }
        //blog表增加评论数
        int commentsCount=blog.getCommentsCount();
        blog.setCommentsCount(commentsCount+1);
        boolean update = blogService.updateById(blog);
        if(!update){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"更新错误");
        }
        return Result.ok(save,"发布评论成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteComment(CommentsIdRequest commentsIdRequest, HttpServletRequest request) {
        if(commentsIdRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Long id = commentsIdRequest.getId();
        if(id==null || id<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        //检验评论是否存在
        Comments comments = this.getById(id);
        if(comments==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"评论不存在");
        }
        //检验用户权限
        User currentUser=userService.getCurrentUser(request);
        if(!Objects.equals(comments.getCommenterId(), currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有权限");
        }
        boolean remove = this.removeById(id);
        if(!remove){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"删除错误");
        }
        //修改blog表的评论数
        Blog blog = blogService.getById(comments.getBlogId());
        int commentsCount=blog.getCommentsCount();
        blog.setCommentsCount(commentsCount-1);
        boolean update = blogService.updateById(blog);
        if(!update){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"更新错误");
        }
        return Result.ok(remove,"删除成功");
    }

    @Override
    public Result searchComment(Long blogId) {
        if(blogId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        LambdaQueryWrapper<Comments> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getBlogId,blogId);
        List<Comments> commentsList = commentsMapper.selectList(queryWrapper);
        //最新评论在最上面
        Collections.reverse(commentsList);
        List<CommentUserResult> commentUserResultList=new ArrayList<>();
        for(Comments comments: commentsList){
            Long commenterId = comments.getCommenterId();
            User commenter=userService.getById(commenterId);
            if(commenter!=null){
                CommentUserResult commentUserResult=new CommentUserResult(comments,commenter);
                commentUserResultList.add(commentUserResult);
            }
        }
        return Result.ok(commentUserResultList);
    }

    @Override
    public Result getCount(Long blogId) {
        if(blogId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        LambdaQueryWrapper<Comments> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getBlogId,blogId);
        long count = this.count(queryWrapper);
        return Result.ok(count);
    }
}




