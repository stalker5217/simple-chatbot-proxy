package com.simplechatbotproxy.chat.controller;

import com.simplechatbotproxy.chat.model.ChatMessageVO;
import com.simplechatbotproxy.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }

    @GetMapping("/welcome")
    public ResponseEntity<ChatMessageVO> welcome(){
        log.info("Handling [/chat/welcome] Start");

        ResponseEntity<ChatMessageVO> ret;
        try{
            ChatMessageVO welcomeMessage = chatService.getWelcomeMessage();
            ret =  new ResponseEntity<>(welcomeMessage, HttpStatus.OK);
        }
        catch(NullPointerException e){
            log.error(e.getMessage());
            ret = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Handling [/chat/welcome] End");

        return ret;
    }
}
