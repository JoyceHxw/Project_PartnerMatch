package com.hxw.partnermatch.config;

import com.hxw.partnermatch.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //拦截器加载时间在spring容器之前，也就是在Bean加载之前就已经加载了拦截器，此时拦截器中注入的Bean为null
    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor()).excludePathPatterns(
                "/user/login",
                "/user/register",
                "/user/sendSms",
                "/user/loginSms");
    }
}
