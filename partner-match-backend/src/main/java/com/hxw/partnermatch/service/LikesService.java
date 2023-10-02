package com.hxw.partnermatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxw.partnermatch.model.Likes;
import com.hxw.partnermatch.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 81086
* @description 针对表【likes(点赞表)】的数据库操作Service
* @createDate 2023-09-28 00:01:29
*/
public interface LikesService extends IService<Likes> {

    /**
     * 点赞功能
     * @param likes
     * @param request
     * @return
     */
    Result addLike(Likes likes, HttpServletRequest request);

    /**
     * 取消点赞
     * @param likes
     * @param request
     * @return
     */
    Result cancelLike(Likes likes, HttpServletRequest request);
}
