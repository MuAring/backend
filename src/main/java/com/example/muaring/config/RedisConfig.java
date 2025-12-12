package com.example.muaring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 로컬 6379 (배포 시 서버 환경에 맞게 host/port는 변경)
        return new LettuceConnectionFactory("localhost", 6379);
    }

    // 숫자 저장용 RedisTemplate
    @Bean
    public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key는 String
        template.setKeySerializer(new StringRedisSerializer());

        // value는 Long
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));

        return template;
    }
}