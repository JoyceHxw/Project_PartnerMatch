package com.hxw.partnermatch.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxw.partnermatch.mapper.UserMapper;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hxw.partnermatch.utils.RedisConstant.CACHE_USER_LOCK_KEY;
import static com.hxw.partnermatch.utils.RedisConstant.RECOMMEND_USER_KEY;

/**
 * 缓存预热，使第一次加载从内存中读取
 */

@Component
@Slf4j
public class PreCacheJob {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    //分布式锁，只有一个服务器操作数据库
    @Autowired
    private RedissonClient redissonClient;

    private List<Long> mainUserList= Arrays.asList(8L);

    @Scheduled(cron="0 35 16 * * *")
    public void doCacheRecommendUser(){
        //像Redis存储数据一样，设置锁的键
        RLock lock=redissonClient.getLock(CACHE_USER_LOCK_KEY);
        try {
            //等待时间为0，只有一个线程能获取到锁
            //注意设置失效时间
            //失效时间设置为-1，利用redisson提供的看门狗，解决程序在未执行情况下，锁失效的问题，监听自动续时30s
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
//                Thread.sleep(300000); 自动续时
                for(Long userId: mainUserList){
                    String redisKey=String.format(RECOMMEND_USER_KEY+"%d",userId);
                    ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
                    //写缓存
                    LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
                    Page<User> page=new Page<>(1,10);
                    Page<User> userPage = userMapper.selectPage(page, queryWrapper);
                    try{
                        //注意设置缓存过期时间，redis内存不能无限期增加
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e){
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        } finally {
            //最后释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
