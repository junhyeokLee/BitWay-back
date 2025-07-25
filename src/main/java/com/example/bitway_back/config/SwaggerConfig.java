package com.example.bitway_back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtAuth = "JWT AUTH";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtAuth);

        Components components = new Components()
                .addSecuritySchemes(jwtAuth, new SecurityScheme()
                        .name(jwtAuth)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization"));

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("KBL Data Entry BackEnd")
                .description("신규 KBL 공식입력기 API 명세서입니다.")
                .version("0.1");
    }
}
