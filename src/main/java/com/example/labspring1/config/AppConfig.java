package com.example.labspring1.config;

import com.example.labspring1.service.RequestCounter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public RequestCounter requestCounter() {
        return new RequestCounter();
    }
}