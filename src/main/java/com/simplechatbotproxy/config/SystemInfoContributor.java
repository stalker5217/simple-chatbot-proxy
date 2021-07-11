package com.simplechatbotproxy.config;

import com.simplechatbotproxy.chat.repository.ChatbotInfoRepository;
import com.simplechatbotproxy.chat.repository.entity.ChatbotInfo;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SystemInfoContributor implements InfoContributor {
    private final ChatbotInfoRepository chatbotInfoRepository;

    public SystemInfoContributor(ChatbotInfoRepository chatbotInfoRepository) {
        this.chatbotInfoRepository = chatbotInfoRepository;
    }

    @Override
    public void contribute(Info.Builder builder) {
        // 개발자 Contract 정보
        Map<String, String> contractMap = new HashMap<>();
        contractMap.put("email", "stalker5217@gmail.com");
        builder.withDetail("contract", contractMap);
        
        // 서비스 챗봇 리스트
        List<ChatbotInfo> chatbotList = new ArrayList<>();
        chatbotInfoRepository.findAll().forEach(chatbotList::add);
        builder.withDetail("chatbot_information", chatbotList);
    }
}