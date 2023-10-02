package com.hxw.partnermatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxw.partnermatch.model.Blog;
import com.hxw.partnermatch.model.requests.BlogIdRequest;
import com.hxw.partnermatch.model.requests.BlogSearchRequest;
import com.hxw.partnermatch.utils.Result;
import jakarta.servlet.http.HttpServletRequest;


/**
* @author 81086
* @description 针对表【blog(博文表)】的数据库操作Service
* @createDate 2023-09-26 18:50:01
*/
public interface BlogService extends IService<Blog> {

    /**
     * 创建博文
     * @param blog 博文
     * @param request 当前用户
     * @return
     */
    Result publishBlog(Blog blog, HttpServletRequest request);

    /**
     * 搜索博文
     * @param blogSearchRequest 搜索条件
     * @param request 当前用户
     * @return
     */
    Result searchBlog(BlogSearchRequest blogSearchRequest, HttpServletRequest request);

    /**
     * 根据id获取博文
     * @param id 博文id
     * @return
     */
    Result getBlogById(Long id,HttpServletRequest request);

    /**
     * 更新博文
     * @param blog 博文
     * @param request 当前用户
     * @return
     */
    Result updateBlog(Blog blog, HttpServletRequest request);

    /**
     * 删除博文
     * @param blogIdRequest 博文id
     * @param request 当前用户
     * @return
     */
    Result deleteBlog(BlogIdRequest blogIdRequest, HttpServletRequest request);
}
