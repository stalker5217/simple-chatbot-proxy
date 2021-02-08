package com.simplechatbotproxy;

import com.simplechatbotproxy.chat.controller.ChatControllerAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@Import(ChatControllerAdvice.class)
public class SimpleChatbotProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleChatbotProxyApplication.class, args);
    }
}
