package com.replya;

import com.replya.config.AnthropicProperties;
import com.replya.config.WhatsAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties({WhatsAppProperties.class, AnthropicProperties.class})
@EnableAsync
public class ReplyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReplyaApplication.class, args);
    }

}
