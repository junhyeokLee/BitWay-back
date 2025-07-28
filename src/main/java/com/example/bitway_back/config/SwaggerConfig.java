package com.example.bitway_back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
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

        // ✅ 서버 URL을 명시적으로 지정 (HTTPS)
        Server server = new Server();
        server.setUrl("https://bitway-back-production.up.railway.app");
        server.setDescription("배포 서버");

        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(server) // 이 라인 추가
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("BITWAY Data Entry BackEnd")
                .description("BitWay API 명세서입니다.")
                .version("0.1");
    }
}
