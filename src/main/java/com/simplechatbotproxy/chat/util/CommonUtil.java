package com.simplechatbotproxy.chat.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommonUtil {
    public String generateChatSessionId(){
            return UUID.randomUUID().toString();
        }
}