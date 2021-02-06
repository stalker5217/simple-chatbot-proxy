package com.simplechatbotproxy.chat.service.impl;

import com.simplechatbotproxy.chat.model.ChatMessageVO;
import com.simplechatbotproxy.chat.service.ChatService;

import com.google.cloud.dialogflow.v2.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DialogflowChatService implements ChatService {
    private static final String WELCOME = "Welcome";

    private final DialogflowCommonService dialogflowCommonService;

    @Autowired
    public DialogflowChatService(DialogflowCommonService dialogflowCommonService){
        this.dialogflowCommonService = dialogflowCommonService;
    }

    /**
     * 챗봇 최초 진입 시 발생하는 Welcome 인사
     * @return ChatMessageVO - 챗봇 질의 결과
     */
    public ChatMessageVO getWelcomeMessage(){
        ChatMessageVO welcomeMessage;

        try{
            QueryResult queryResult = dialogflowCommonService.detectIntentByEvent(WELCOME);

            welcomeMessage = new ChatMessageVO();
            welcomeMessage.setText(queryResult.getFulfillmentText());
        }
        catch(IOException e){
            log.error(e.getMessage());
            throw new NullPointerException("Cannot resolve welcome message");
        }

        return welcomeMessage;
    }
}