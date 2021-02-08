package com.simplechatbotproxy.chat.service.impl;

import com.google.cloud.dialogflow.v2.*;
import com.simplechatbotproxy.chat.model.QueryMessage;
import com.simplechatbotproxy.chat.model.ResultMessage;
import com.simplechatbotproxy.chat.repository.ConversationHistoryRepository;
import com.simplechatbotproxy.chat.repository.SessionHistoryRepository;
import com.simplechatbotproxy.chat.repository.entity.ChatbotInfo;
import com.simplechatbotproxy.chat.repository.entity.ConversationHistory;
import com.simplechatbotproxy.chat.repository.entity.SessionHistory;
import com.simplechatbotproxy.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class DialogflowChatServiceImpl implements ChatService {
    private static final String WELCOME = "Welcome";

    private final BotInfoService botInfoService;
    private final SessionHistoryRepository sessionHistoryRepository;
    private final ConversationHistoryRepository conversationHistoryRepository;

    @Autowired
    public DialogflowChatServiceImpl(
            BotInfoService botInfoService,
            SessionHistoryRepository sessionHistoryRepository,
            ConversationHistoryRepository conversationHistoryRepository)
    {
        this.botInfoService = botInfoService;
        this.sessionHistoryRepository = sessionHistoryRepository;
        this.conversationHistoryRepository = conversationHistoryRepository;
    }

    public ResultMessage getWelcomeMessage(QueryMessage queryMessage) throws IOException {
        setTargetLanguageCode(queryMessage);
        queryMessage.setEvent(WELCOME);
        QueryResult queryResult = detectIntentByEvent(queryMessage);

        ResultMessage welcomeMessage = new ResultMessage();
        welcomeMessage.setText(queryResult.getFulfillmentText());

        SessionHistory sessionHistory =
                SessionHistory
                        .builder()
                        .chatSessionId(queryMessage.getChatSessionId())
                        .botId(queryMessage.getTargetBot())
                        .build();

        sessionHistoryRepository.save(sessionHistory);

        ConversationHistory conversationHistory
                = ConversationHistory
                .builder()
                .chatSessionId(queryMessage.getChatSessionId())
                .event(queryMessage.getEvent())
                .responseText(queryResult.getFulfillmentText())
                .intentName(queryResult.getIntent().getDisplayName())
                .fallbackFlag(queryResult.getIntent().getIsFallback())
                .intentDetectionConfidence(queryResult.getIntentDetectionConfidence())
                .build();

        conversationHistoryRepository.save(conversationHistory);

        return welcomeMessage;
    }

    public ResultMessage getQueryResultMessage(QueryMessage queryMessage) throws IOException{
        setTargetLanguageCode(queryMessage);

        QueryResult queryResult = detectIntentByText(queryMessage);

        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setText(queryResult.getFulfillmentText());

        ConversationHistory conversationHistory
                = ConversationHistory
                .builder()
                .chatSessionId(queryMessage.getChatSessionId())
                .queryText(queryMessage.getQueryText())
                .responseText(queryResult.getFulfillmentText())
                .intentName(queryResult.getIntent().getDisplayName())
                .fallbackFlag(queryResult.getIntent().getIsFallback())
                .intentDetectionConfidence(queryResult.getIntentDetectionConfidence())
                .build();

        conversationHistoryRepository.save(conversationHistory);

        return resultMessage;
    }

    private QueryResult detectIntentByEvent(QueryMessage queryMessage) throws IOException {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName sessionName = SessionName.of(queryMessage.getTargetBot(), queryMessage.getChatSessionId());

            log.info(String.format("Session Path : %s", sessionName.toString()));

            EventInput eventInput = EventInput
                    .newBuilder()
                    .setLanguageCode(queryMessage.getLanguageCode())
                    .setName(queryMessage.getEvent())
                    .build();

            QueryInput queryInput = QueryInput
                    .newBuilder()
                    .setEvent(eventInput).build();

            return detectIntent(sessionsClient, sessionName, queryInput);
        }
    }

    private QueryResult detectIntentByText(QueryMessage queryMessage) throws IOException{
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName sessionName = SessionName.of(queryMessage.getTargetBot(), queryMessage.getChatSessionId());

            log.info(String.format("Session Path : %s", sessionName.toString()));

            TextInput textInput = TextInput
                    .newBuilder()
                    .setLanguageCode(queryMessage.getLanguageCode())
                    .setText(queryMessage.getQueryText())
                    .build();

            QueryInput queryInput = QueryInput
                    .newBuilder()
                    .setText(textInput).build();

            return detectIntent(sessionsClient, sessionName, queryInput);
        }
    }

    private QueryResult detectIntent(SessionsClient sessionsClient, SessionName sessionName, QueryInput queryInput){
        DetectIntentResponse response = sessionsClient.detectIntent(sessionName, queryInput);
        QueryResult queryResult = response.getQueryResult();

        log.info("====================");
        log.info(String.format("Query Text: %s", queryResult.getQueryText()));
        log.info(String.format(
                "Detected Intent: %s (confidence: %f)",
                queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence()));
        log.info(String.format("Fulfillment Text: '%s'", queryResult.getFulfillmentText()));
        log.info("====================");

        return queryResult;
    }

    private void setTargetLanguageCode(QueryMessage queryMessage){
        Optional<ChatbotInfo> botInfo = botInfoService.getBotInfo(queryMessage.getTargetBot());
        if(botInfo.isPresent()){
            queryMessage.setLanguageCode(botInfo.get().getLanguageCode());
        }
        else{
            throw new IllegalArgumentException("CAN NOT RESOLVE TARGET CHATBOT INFORMATION");
        }
    }
}