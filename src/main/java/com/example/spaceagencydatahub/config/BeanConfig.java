package com.example.spaceagencydatahub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.logger.controller.aspect.GenericControllerAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {
    @Bean
    public GenericControllerAspect genericControllerAspect() {
        GenericControllerAspect aspect = new GenericControllerAspect();
        aspect.setEnableDataScrubbing(true);
        return new GenericControllerAspect();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
