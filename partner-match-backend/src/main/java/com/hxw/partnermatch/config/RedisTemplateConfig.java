package com.hxw.partnermatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 自定义序列化
 * 数据丢失：Redis是一个键值存储数据库，它期望存储的值是二进制数据（或字符串）。
 *      如果您将未序列化的POJO对象直接存储在Redis中，Redis将不知道如何处理这个对象，并且存储的值可能会被破坏或丢失。
 *
 * 数据不可读：未序列化的POJO对象可能包含各种复杂的数据结构、字段和引用，这些结构在Redis中无法直接表示。
 *      如果您尝试从Redis中获取未序列化的对象，将得到一个无法解释的二进制字符串，无法正确地还原为原始POJO对象。
 *
 * 效率问题：Redis之所以如此高效，部分原因是因为它可以将数据存储在内存中，并且能够迅速进行读写操作。未
 *      序列化的POJO对象通常会占用大量内存空间，并且无法高效地存储在Redis中。
 */

@Configuration
public class RedisTemplateConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>();
        //设置连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        //创建JSON序列化工具
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer=new GenericJackson2JsonRedisSerializer();
        //设置key的序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        //设置value的序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }
}
