package com.civi.cms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins}")      // property:  http://localhost:3000,https://cmsservice-…
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = allowedOrigins.split(",");
                registry.addMapping("/**")
                        .allowedOrigins(origins)      // use your property, not "*"
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*");
                // .allowCredentials(true);   // enable only if you *need* cookies/Authorization header,
                // then remove "*" and keep explicit origins
            }
        };
    }
}

