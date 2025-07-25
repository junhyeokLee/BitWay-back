package com.example.bitway_back.config;

import com.example.bitway_back.util.StringStripJsonDeserializer;
import com.example.bitway_back.util.StringStripJsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JsonConfig {
    @Bean
    public ObjectMapper objectMapper(){
        return Jackson2ObjectMapperBuilder
                .json()
                .modules(customJsonDeserializeModule())
                .modules(customJsonSerializeModule())
                .build();
    }

    private SimpleModule customJsonDeserializeModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringStripJsonDeserializer());

        return module;
    }

    private SimpleModule customJsonSerializeModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new StringStripJsonSerializer());

        return module;
    }
}
