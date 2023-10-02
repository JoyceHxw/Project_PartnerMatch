package com.hxw.partnermatch.service;

import com.hxw.partnermatch.mapper.UserMapper;
import com.hxw.partnermatch.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Test
    void db() {
        User user=new User();
        user.setUsername("hxw");
        user.setAccount("18681693111");
        user.setGender(0);
        user.setPhone("18681693111");
        user.setPassword("123123123");
        user.setEmail("810861304@qq.com");
        user.setStatus(0);
        boolean result = userService.save(user);
        Assertions.assertTrue(result);
//        List<User> userList=userMapper.selectList(null);
//        System.out.println(userList);
    }

    @Test
    void register() {

    }
}