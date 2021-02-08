package com.simplechatbotproxy.chat.service.impl;

import com.simplechatbotproxy.chat.repository.ChatbotInfoRepository;
import com.simplechatbotproxy.chat.repository.entity.ChatbotInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BotInfoService {
    private Map<String, ChatbotInfo> botInfoMap;
    private final ChatbotInfoRepository chtbotInfoRepository;

    @Autowired
    public BotInfoService(ChatbotInfoRepository chatbotInfoRepository){
        this.chtbotInfoRepository = chatbotInfoRepository;
    }

    @PostConstruct
    private void init(){
        botInfoMap = Collections.synchronizedMap(new HashMap<>());
        botInfoMap = StreamSupport
                .stream(chtbotInfoRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(ChatbotInfo::getBotId, Function.identity()));
    }

    public Optional<ChatbotInfo> getBotInfo(String botId){
        return Optional
                .ofNullable(botInfoMap.get(botId));
    }
}
