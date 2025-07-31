package com.example.bitway_back.config;

import com.example.bitway_back.socket.subscriber.RedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscriberConfig {

    @Autowired
    private RedisMessageSubscriber subscriber;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 심볼은 런타임에 추가할 수 있게 pattern 기반 구독
        container.addMessageListener(subscriber, new PatternTopic("trade:*"));

        return container;
    }
}