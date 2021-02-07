package com.simplechatbotproxy.chat.service.impl;

import com.simplechatbotproxy.chat.service.CommonService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {
    public String generateChatSessionId(){
        return UUID.randomUUID().toString();
    }
}