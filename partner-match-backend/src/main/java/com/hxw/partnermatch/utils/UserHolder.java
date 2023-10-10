package com.hxw.partnermatch.utils;

import com.hxw.partnermatch.model.User;

/**
 * ThreadLocal叫做线程变量，意思是ThreadLocal中填充的变量属于当前线程，该变量对其他线程而言是隔离的，也就是说该变量是当前线程独有的变量。
 * ThreadLocal为变量在每个线程中都创建了一个副本，那么每个线程可以访问自己内部的副本变量。
 */
public class UserHolder {
    private static final ThreadLocal<User> tl=new ThreadLocal<>();
    public static void saveUser(User user){
        tl.set(user);
    }
    public static User getUser(){
        return tl.get();
    }
    public static void removeUser(){
        tl.remove();
    }
}
