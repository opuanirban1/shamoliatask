package com.dev.aes;

import com.dev.aes.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class OcrdmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcrdmsApplication.class, args);
    }

}