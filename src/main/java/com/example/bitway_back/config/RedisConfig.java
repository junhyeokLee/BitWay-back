package com.example.bitway_back.config;
import com.example.bitway_back.domain.market.LongShortRatio;
import com.example.bitway_back.domain.market.SentimentIndex;
import com.example.bitway_back.dto.response.KimchiPremiumResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(host, port);
        factory.setPassword(password);
        return factory;
    }


    @Bean
    public RedisTemplate<String, KimchiPremiumResDto> kimchiPremiumRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, KimchiPremiumResDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 String으로 처리
        template.setKeySerializer(new StringRedisSerializer());

        // Value는 Jackson JSON으로 처리
        Jackson2JsonRedisSerializer<KimchiPremiumResDto> valueSerializer = new Jackson2JsonRedisSerializer<>(KimchiPremiumResDto.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        valueSerializer.setObjectMapper(objectMapper);

        template.setValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, SentimentIndex> sentimentIndexRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, SentimentIndex> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Jackson serializer 설정
        Jackson2JsonRedisSerializer<SentimentIndex> serializer = new Jackson2JsonRedisSerializer<>(SentimentIndex.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ← LocalDateTime 처리 가능하게
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 대신 ISO8601 형식 사용
        serializer.setObjectMapper(objectMapper);

        // 키와 값 직렬화기 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, LongShortRatio> longShortRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, LongShortRatio> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Jackson serializer 설정
        Jackson2JsonRedisSerializer<LongShortRatio> serializer = new Jackson2JsonRedisSerializer<>(LongShortRatio.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ← LocalDateTime 처리 가능하게
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 대신 ISO8601 형식 사용
        serializer.setObjectMapper(objectMapper);

        // 키와 값 직렬화기 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}