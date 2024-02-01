package com.coder.talkjohns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // set key's serialization method
        template.setKeySerializer(RedisSerializer.string());
        // set value's serialization method
        template.setValueSerializer(RedisSerializer.json());
        // set hash key's serialization method
        template.setHashKeySerializer(RedisSerializer.string());
        // set hash value's serialization method
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }

}









































