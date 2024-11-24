package com.dev.aes.email_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Configuration
public class FreeMarkerConfig {
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configure = new FreeMarkerConfigurer();
        configure.setTemplateLoaderPath("classpath:/templates/");
        return configure;
    }
}
