package com.simplechatbotproxy.chat.service.impl;

import com.google.cloud.dialogflow.v2.*;
import com.simplechatbotproxy.chat.model.QueryMessage;
import com.simplechatbotproxy.chat.model.ResultMessage;
import com.simplechatbotproxy.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DialogflowChatServiceImpl implements ChatService {
    private static final String WELCOME = "Welcome";
    private static final String NULL_POINTER_EXCEPTION_MSG = "Cannot resolve welcome message";

    public ResultMessage getWelcomeMessage(QueryMessage queryMessage){
        ResultMessage welcomeMessage;

        try{
            queryMessage.setEvent(WELCOME);
            QueryResult queryResult = detectIntentByEvent(queryMessage);

            welcomeMessage = new ResultMessage();
            welcomeMessage.setText(queryResult.getFulfillmentText());
        }
        catch(IOException e){
            log.error(e.getMessage());
            throw new NullPointerException(NULL_POINTER_EXCEPTION_MSG);
        }

        return welcomeMessage;
    }

    public ResultMessage getQueryResultMessage(QueryMessage queryMessage){
        ResultMessage resultMessage;

        try{
            QueryResult queryResult = detectIntentByText(queryMessage);

            resultMessage = new ResultMessage();
            resultMessage.setText(queryResult.getFulfillmentText());
        }
        catch(IOException e){
            log.error(e.getMessage());
            throw new NullPointerException(NULL_POINTER_EXCEPTION_MSG);
        }

        return resultMessage;
    }

    private QueryResult detectIntentByEvent(QueryMessage queryMessage) throws IOException {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName sessionName = SessionName.of(queryMessage.getTargetBot(), queryMessage.getChatSessionId());

            log.info(String.format("Session Path : %s", sessionName.toString()));

            EventInput eventInput = EventInput
                    .newBuilder()
                    .setLanguageCode("ko-KR")
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
                    .setLanguageCode("ko-KR")
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
}