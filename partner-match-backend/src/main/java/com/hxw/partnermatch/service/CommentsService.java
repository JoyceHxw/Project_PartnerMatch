package com.hxw.partnermatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxw.partnermatch.model.Comments;
import com.hxw.partnermatch.model.requests.CommentsIdRequest;
import com.hxw.partnermatch.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 81086
* @description 针对表【comments(评论表)】的数据库操作Service
* @createDate 2023-09-27 18:24:40
*/
public interface CommentsService extends IService<Comments> {

    /**
     * 发布评论
     * @param comments 评论
     * @param request 用户
     * @return
     */
    Result publishComment(Comments comments, HttpServletRequest request);

    /**
     * 删除评论
     * @param commentsIdRequest
     * @param request
     * @return
     */
    Result deleteComment(CommentsIdRequest commentsIdRequest, HttpServletRequest request);

    /**
     * 展示评论
     * @param blogId 博文id
     * @return
     */
    Result searchComment(Long blogId);

    /**
     * 统计博文的评论数
     * @param blogId
     * @return
     */
    Result getCount(Long blogId);
}
