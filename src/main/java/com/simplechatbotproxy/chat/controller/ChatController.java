package com.simplechatbotproxy.chat.controller;

import com.simplechatbotproxy.chat.model.QueryMessage;
import com.simplechatbotproxy.chat.model.ResultMessage;
import com.simplechatbotproxy.chat.service.ChatService;
import com.simplechatbotproxy.chat.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {
    private static final String INVALID_REQUEST_LOG = "Invalid Request";
    private static final String CHAT_SESSION_ID = "Chat-Session-Id";

    private final CommonService commonService;
    private final ChatService chatService;

    @Autowired
    public ChatController(CommonService commonService, ChatService chatService){
        this.commonService = commonService;
        this.chatService = chatService;
    }

    @GetMapping("/welcome")
    public ResponseEntity<ResultMessage> welcome(@RequestParam String targetBot){
        log.info("Handling [/chat/welcome] Start");

        ResponseEntity<ResultMessage> ret;
        try{
            String chatSessionId = commonService.generateChatSessionId();

            QueryMessage queryMessage = new QueryMessage();
            queryMessage.setTargetBot(targetBot);
            queryMessage.setChatSessionId(chatSessionId);

            ResultMessage welcomeMessage = chatService.getWelcomeMessage(queryMessage);

            ret = ResponseEntity
                    .ok()
                    .header(CHAT_SESSION_ID, chatSessionId)
                    .body(welcomeMessage);
        }
        catch(NullPointerException e){
            log.error(e.getMessage());
            ret = ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR)).build();
        }

        log.info("Handling [/chat/welcome] End");

        return ret;
    }

    @GetMapping("/query")
    public ResponseEntity<ResultMessage> chatQuery(
            @ModelAttribute @Valid QueryMessage queryMessage,
            Errors errors){

        log.info("Handling [/chat/query] Start");

        if(errors.hasErrors()){
            log.warn(INVALID_REQUEST_LOG);
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<ResultMessage> ret;
        try{
            ResultMessage resultMessage = chatService.getQueryResultMessage(queryMessage);

            ret = ResponseEntity
                    .ok()
                    .body(resultMessage);
        }
        catch(NullPointerException e){
            log.error(e.getMessage());
            ret = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("Handling [/chat/query] End");

        return ret;
    }
}
