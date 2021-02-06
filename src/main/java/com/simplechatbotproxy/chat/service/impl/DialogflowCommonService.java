package com.simplechatbotproxy.chat.service.impl;

import com.google.cloud.dialogflow.v2.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class DialogflowCommonService {
    public QueryResult detectIntentByEvent(String eventName) throws IOException {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            UUID uuid = UUID.randomUUID();
            SessionName sessionName = SessionName.of("song-chat-service", uuid.toString());

            log.info(String.format("Session Path : %s", sessionName.toString()));

            EventInput eventInput = EventInput
                    .newBuilder()
                    .setLanguageCode("ko-KR")
                    .setName(eventName)
                    .build();

            QueryInput queryInput = QueryInput
                    .newBuilder()
                    .setEvent(eventInput).build();

            return detectIntent(sessionsClient, sessionName, queryInput);
        }
    }

    public QueryResult detectIntentByText(String text) throws IOException{
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            UUID uuid = UUID.randomUUID();
            SessionName sessionName = SessionName.of("song-chat-service", uuid.toString());

            log.info(String.format("Session Path : %s", sessionName.toString()));

            TextInput textInput = TextInput
                    .newBuilder()
                    .setLanguageCode("ko-KR")
                    .setText(text)
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
