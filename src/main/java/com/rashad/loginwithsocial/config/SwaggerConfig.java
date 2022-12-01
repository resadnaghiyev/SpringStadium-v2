package com.rashad.loginwithsocial.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${base.url}")
    private String baseUrl;

    @Bean
    public OpenAPI springStadiumOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stadium Rest API")
                        .description("Stadium reservation application")
                        .version("v0.0.1")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Our Web Site")
                        .url("https://stadium-booking.vercel.app"))
                .servers(List.of(
                        new Server().description("Base URL").url(baseUrl + "/api/v1")))
                .components(new Components()
                        .addSecuritySchemes("BearerJwt", new SecurityScheme()
                                .name("BearerJwt")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Bearer token for the authorization")));
    }
}



//                .addSecurityItem(new SecurityRequirement().addList("BearerJwt"))