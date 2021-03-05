package com.simplechatbotproxy.chat.controller;

import com.simplechatbotproxy.chat.model.QueryMessage;
import com.simplechatbotproxy.chat.model.ResultMessage;
import com.simplechatbotproxy.chat.service.ChatService;
import com.simplechatbotproxy.chat.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final CommonService commonService;
    private final ChatService chatService;

    @Autowired
    public ChatController(CommonService commonService, ChatService chatService){
        this.commonService = commonService;
        this.chatService = chatService;
    }

    @GetMapping("/welcome")
    public ResponseEntity<EntityModel<ResultMessage>> welcome(@RequestParam String targetBot) throws IOException {
        log.info("Handling [/chat/welcome] Start");

        String chatSessionId = commonService.generateChatSessionId();

        QueryMessage queryMessage = new QueryMessage();
        queryMessage.setTargetBot(targetBot);
        queryMessage.setChatSessionId(chatSessionId);

        ResultMessage welcomeMessage = chatService.getWelcomeMessage(queryMessage);

        EntityModel<ResultMessage> entityModel = EntityModel.of(welcomeMessage);
        entityModel.add(linkTo(ChatController.class).withSelfRel());
        entityModel.add(linkTo(ChatController.class).withRel("query"));

        ResponseEntity<EntityModel<ResultMessage>> ret = ResponseEntity
                .ok()
                .header("CHAT_SESSION_ID", chatSessionId)
                .body(entityModel);

        log.info("Handling [/chat/welcome] End");

        return ret;
    }

    @GetMapping("/query")
    public ResponseEntity<EntityModel<ResultMessage>> chatQuery(
            @ModelAttribute @Valid QueryMessage queryMessage,
            Errors errors) throws IOException{

        log.info("Handling [/chat/query] Start");

        if(errors.hasErrors()){
            log.error("INVALID_REQUEST");
            return ResponseEntity.badRequest().build();
        }

        ResultMessage resultMessage = chatService.getQueryResultMessage(queryMessage);

        EntityModel<ResultMessage> entityModel = EntityModel.of(resultMessage);
        entityModel.add(linkTo(ChatController.class).withSelfRel());
        entityModel.add(linkTo(ChatController.class).withRel("query"));

        ResponseEntity<EntityModel<ResultMessage>> ret = ResponseEntity
                .ok()
                .body(entityModel);

        log.info("Handling [/chat/query] End");

        return ret;
    }
}
