package com.hxw.partnermatch.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import com.hxw.partnermatch.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hxw.partnermatch.utils.RedisConstant.LOGIN_USER_TOKEN_KEY;
import static com.hxw.partnermatch.utils.RedisConstant.LOGIN_USER_TOKEN_TTL;
import static com.hxw.partnermatch.utils.UserConstant.USER_LOGIN_STATE;


public class LoginInterceptor implements HandlerInterceptor {

    private RedisTemplate<String,Object> redisTemplate;
    public LoginInterceptor(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置跨域响应头
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // 过滤预检请求，预检请求不携带请求头
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return true;
        }

        String token=request.getHeader("Authorization");
        if(StringUtils.isBlank(token)){
            writeResponse(response);
//            response.setStatus(503);
            return false;
        }
//        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(LOGIN_USER_TOKEN_KEY + token);
        User user = (User) redisTemplate.opsForValue().get(LOGIN_USER_TOKEN_KEY + token);
//        if(userMap.isEmpty()){
//            writeResponse(response);
////            response.setStatus(503);
//            return false;
//        }
//        User user = BeanUtil.fillBeanWithMap(userMap, new User(), false);
        if(user==null){
            writeResponse(response);
            return false;
        }
        UserHolder.saveUser(user);
        //刷新有效期
        redisTemplate.expire(LOGIN_USER_TOKEN_KEY+token,LOGIN_USER_TOKEN_TTL, TimeUnit.MINUTES);
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user=(User) userObj;
//        if(user!=null){
//            return true;
//        }
        //写回response
//        Result result= Result.build(null,ResultCodeEnum.NO_AUTH,"未登录");
//        ObjectMapper objectMapper=new ObjectMapper();
//        String json=objectMapper.writeValueAsString(result);
//        response.getWriter().print(json);
        return true;
    }

    private void writeResponse(HttpServletResponse response) throws IOException {
        Result result= Result.build(null,ResultCodeEnum.NO_AUTH,"未登录");
        ObjectMapper objectMapper=new ObjectMapper();
        String json=objectMapper.writeValueAsString(result);
        response.getWriter().print(json);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除用户
        UserHolder.removeUser();
    }
}
