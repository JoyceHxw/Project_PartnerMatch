package com.hxw.partnermatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.hxw.partnermatch.mapper")
@SpringBootApplication
@EnableScheduling
public class PartnerMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerMatchApplication.class, args);
    }

}
