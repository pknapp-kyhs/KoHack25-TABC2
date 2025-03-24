package com.judahharris.kohack2025.screen;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScreenConfig {

    @Bean
    public ScreenContext screenContext() {
        return new ScreenContext();
    }
}