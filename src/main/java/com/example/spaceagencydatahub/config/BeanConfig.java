package com.example.spaceagencydatahub.config;

import io.github.logger.controller.aspect.GenericControllerAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public GenericControllerAspect genericControllerAspect() {
        GenericControllerAspect aspect = new GenericControllerAspect();
        aspect.setEnableDataScrubbing(true);
        return new GenericControllerAspect();
    }
}
