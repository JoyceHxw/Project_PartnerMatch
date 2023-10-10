package com.hxw.partnermatch.utils;

public class RedisConstant {
    /**
     * 手机验证码
     */
    public static final String LOGIN_CODE_KEY="hxw:partnermatch:login:code:";
    /**
     * 验证码有效期
     */
    public static final Long LOGIN_CODE_TTL=5L;
    /**
     * 用户token
     */
    public static final String LOGIN_USER_TOKEN_KEY="hxw:partnermatch:login:token:";
    /**
     * token有效期
     */
    public static final Long LOGIN_USER_TOKEN_TTL=30L;
    /**
     * redisson分布式锁
     */
    public static final String CACHE_USER_LOCK_KEY="hxw:partnermatch:cache:lock";
    /**
     * 预热推荐用户列表
     */
    public static final String RECOMMEND_USER_KEY="hxw:partnermatch:recommend:";
    /**
     * 私信聊天记录
     */
    public static final String CHAT_RECORD_PRIVATE_KEY="hxw:partnermatch:chat:private:";
    /**
     * 群聊记录
     */
    public static final String CHAT_RECORD_TEAM_KEY="hxw:partnermatch:chat:team:";
}
