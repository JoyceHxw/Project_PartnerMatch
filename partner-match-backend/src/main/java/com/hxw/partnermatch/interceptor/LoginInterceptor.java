package com.hxw.partnermatch.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.hxw.partnermatch.utils.UserConstant.USER_LOGIN_STATE;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置跨域响应头
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if(user!=null){
            return true;
        }
        //写回response
        Result result= Result.build(null,ResultCodeEnum.NO_AUTH,"未登录");
        ObjectMapper objectMapper=new ObjectMapper();
        String json=objectMapper.writeValueAsString(result);
        response.getWriter().print(json);
        return false;
    }
}
