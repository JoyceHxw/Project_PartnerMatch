package com.hxw.partnermatch.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Team;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.TagsSearchRequest;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.model.requests.LoginSmsRequest;
import com.hxw.partnermatch.model.requests.RegisterRequest;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户接口
 * @author hxw
 */

@Tag(name = "用户管理")
@RestController
@RequestMapping("user")
@CrossOrigin(origins = "http://127.0.0.1:5173",allowCredentials = "true") //解决跨域携带cookie的问题
public class UserController {
    @Autowired
    private UserService userService;

    //注册
    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest registerRequest){
        User user=registerRequest.getUser();
        String checkPassword=registerRequest.getCheckPassword();
        Result result=userService.register(user,checkPassword);
        return result;
    }

    //登录
    @PostMapping("login")
    public Result login(@RequestBody User user, HttpServletRequest request){
        Result result=userService.login(user,request);
        return result;
    }

    //短信验证码登录
    @GetMapping("sendSms")
    public Result sendSms(@RequestParam String phone){
        Result result=userService.sendSms(phone);
        return result;
    }
    @PostMapping("loginSms")
    public Result loginSms(@RequestBody LoginSmsRequest loginSmsRequest, HttpServletRequest request){
        String phone=loginSmsRequest.getPhone();
        Integer code=loginSmsRequest.getCode();
        Result result=userService.loginSms(phone,code,request);
        return result;
    }

    //获取当前用户
    @GetMapping("currentUser")
    public Result getCurrentUser(HttpServletRequest request){
        User user=userService.getCurrentUser(request);
        return Result.ok(user);
    }

    @GetMapping("getById")
    public Result getUserById(@RequestParam Long id){
        if(id==null || id<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍参数错误");
        }
        User user = userService.getById(id);
        return Result.ok(user,"获取成功");
    }

    //通过用户名查询用户
    @GetMapping("searchByUsername")
    public Result searchByUsername(String username,HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有管理员权限");
//            return Result.build(null,500,"not administrator");
        }
        //查询
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like(User::getUsername,username);
        }
        List<User> userList=userService.list(queryWrapper);
        //数据脱敏
        List<User> safeUserList=userService.getSafeUserList(userList);
        return Result.ok(safeUserList);
    }

    //删除用户
    @PostMapping("delete")
    public Result delete(long id,HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有管理员权限");
//            return Result.build(null,500,"not administrator");
        }
        if(id<=0) {
            return null;
        }
        boolean result = userService.removeById(id);
        if(!result){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"删除失败");
        }
        return Result.ok(result);
    }

    //注销登出
    @PostMapping("logout")
    public Result logout(HttpServletRequest request){
        if(request==null){
            return null;
        }
        return userService.logout(request);
    }

    //通过标签查询
    @PostMapping("searchByTag")
    public Result searchByTag(@RequestBody TagsSearchRequest tagsSearchRequest){
        if(tagsSearchRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=userService.searchByTag(tagsSearchRequest);
        return result;
    }

    //更新用户信息
    @PostMapping("update")
    public Result updateUser(@RequestBody User user,HttpServletRequest request){
        Result result=userService.updateUser(user,request);
        return result;
    }

    //首页推荐用户
    @PostMapping("recommend")
    public Result recommendUsers(@RequestBody TagsSearchRequest tagsSearchRequest, HttpServletRequest request){
        Result result=userService.recommendUsers(tagsSearchRequest,request);
        return result;

    }

}
