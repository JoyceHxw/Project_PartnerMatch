package com.hxw.partnermatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.TagsSearchRequest;
import com.hxw.partnermatch.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 81086
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-09-08 16:49:05
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param user 用户实例
     * @return 操作结果
     */
    Result register(User user,String checkPassword);

    /**
     * 用户登录
     * @param user 用户实例
     * @return 操作结果
     */
    Result login(User user, HttpServletRequest request);

    /**
     * 用户注销
     */
    Result logout(HttpServletRequest request);

    /**
     * 短信验证登录
     * @param phone 电话
     * @param code 验证码
     * @param request session
     * @return result
     */
    Result loginSms(String phone, Integer code, HttpServletRequest request);

    /**
     * 发送验证码
     * @param phone 电话
     * @return result
     */
    Result sendSms(String phone);

    /**
     * 根据标签搜索用户
     * @param tagsSearchRequest json标签列表，和分页查询参数
     * @return result
     */
    Result searchByTag(TagsSearchRequest tagsSearchRequest);

    /**
     * 更新用户
     * @param user 用户信息
     * @return result
     */
    Result updateUser(User user,HttpServletRequest request);

    /**
     * 通过session获取用户角色，鉴权
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 根据session获取当前登录用户
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 首页推荐用户，根据编辑距离匹配标签
     * 分页查询
     * @param tagsSearchRequest 分页参数
     * @param request
     * @return
     */
    Result recommendUsers(TagsSearchRequest tagsSearchRequest,HttpServletRequest request);

    /**
     * 用户信息脱敏
     */
    List<User> getSafeUserList(List<User> userList);
}
