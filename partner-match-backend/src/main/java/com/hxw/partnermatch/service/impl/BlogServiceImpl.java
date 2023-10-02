package com.hxw.partnermatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.mapper.BlogMapper;
import com.hxw.partnermatch.mapper.LikesMapper;
import com.hxw.partnermatch.model.Blog;
import com.hxw.partnermatch.model.Likes;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.BlogIdRequest;
import com.hxw.partnermatch.model.requests.BlogSearchRequest;
import com.hxw.partnermatch.model.responses.BlogUserResult;
import com.hxw.partnermatch.service.BlogService;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author 81086
* @description 针对表【blog(博文表)】的数据库操作Service实现
* @createDate 2023-09-26 18:50:01
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Override
    public Result publishBlog(Blog blog, HttpServletRequest request) {
        if(blog==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        User currentUser=userService.getCurrentUser(request);
        //验证标题和内容是否为空
        if(StringUtils.isBlank(blog.getTitle()) || StringUtils.isBlank(blog.getContent())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"标题或内容为空");
        }
        //验证标题的长度
        if(blog.getTitle().length()>25){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"标题过长");
        }
        blog.setAuthorId(currentUser.getId());
        boolean save = this.save(blog);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"博文保存错误");
        }
        return Result.ok(save,"发布成功！");
    }

    @Override
    public Result searchBlog(BlogSearchRequest blogSearchRequest, HttpServletRequest request) {
        User currentUser=userService.getCurrentUser(request);
        LambdaQueryWrapper<Blog> queryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        if(blogSearchRequest!=null){
            Long authorId = blogSearchRequest.getAuthorId();
            if(authorId!=null && authorId>0){
                queryWrapper.eq(Blog::getAuthorId,authorId);
            }
            String searchText = blogSearchRequest.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw->qw.like(Blog::getTitle,searchText).or().like(Blog::getContent,searchText));
            }
        }
        //封装结果
        List<BlogUserResult> blogUserResultList=new ArrayList<>();
        for (Blog blog : this.list(queryWrapper)) {
            Long authorId = blog.getAuthorId();
            User author=userService.getById(authorId);
            BlogUserResult blogUserResult=new BlogUserResult(blog,author);
            //查询当前用户是否对这条博文点赞过
            findIsLiked(blog, currentUser, blogUserResult);
            blogUserResultList.add(blogUserResult);
        }
        //翻转，最新发布的在最上面
        Collections.reverse(blogUserResultList);
        return Result.ok(blogUserResultList,"查询成功！");
    }

    @Override
    public Result getBlogById(Long id,HttpServletRequest request) {
        if(id==null || id<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        Blog blog = this.getById(id);
        if(blog==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"未找到博文信息");
        }
        User author = userService.getById(blog.getAuthorId());
        if(author==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"未找到作者信息");
        }
        BlogUserResult blogUserResult=new BlogUserResult(blog,author);
        //查询当前用户是否对这条博文点赞过
        User currentUser=userService.getCurrentUser(request);
        findIsLiked(blog,currentUser,blogUserResult);
        return Result.ok(blogUserResult,"查询成功！");
    }

    /**
     * 查询当前用户是否对这条博文点赞过
     *
     * @param blog           博文
     * @param currentUser    当前用户
     * @param blogUserResult 返回结果
     */
    private void findIsLiked(Blog blog, User currentUser, BlogUserResult blogUserResult) {
        LambdaQueryWrapper<Likes> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Likes::getBlogId, blog.getId()).eq(Likes::getUserId, currentUser.getId());
        Likes one = likesMapper.selectOne(queryWrapper1);
        if(one==null){
            blogUserResult.setIsLikedByCurrentUser(false);
        }
        else{
            blogUserResult.setIsLikedByCurrentUser(true);
        }
    }

    @Override
    public Result updateBlog(Blog blog, HttpServletRequest request) {
        if(blog==null || blog.getId()==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //检验标题内容是否为空
        if(StringUtils.isBlank(blog.getTitle()) || StringUtils.isBlank(blog.getContent())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"标题或内容为空");
        }
        //检验权限，当前用户是否是作者
        User currentUser=userService.getCurrentUser(request);
        if(!Objects.equals(blog.getAuthorId(), currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"不是博文作者");
        }
        blog.setUpdateTime(new Date());
        int result = blogMapper.updateById(blog);
        if(result==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"更新出错");
        }
        return Result.ok(result,"更新成功！");
    }

    @Override
    public Result deleteBlog(BlogIdRequest blogIdRequest, HttpServletRequest request) {
        if(blogIdRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Long id = blogIdRequest.getId();
        if(id==null || id<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        //验证权限
        Blog blog=getById(id);
        if(blog==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"该博文不存在");
        }
        User currentUser=userService.getCurrentUser(request);
        if(!Objects.equals(blog.getAuthorId(), currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"不是博文作者");
        }
        boolean result = this.removeById(id);
        if(!result){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"删除错误");
        }
        return Result.ok(result,"删除成功！");
    }
}




