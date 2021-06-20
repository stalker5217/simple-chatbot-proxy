package com.simplechatbotproxy.chat.controller;

import com.simplechatbotproxy.chat.repository.SessionHistoryRepository;
import com.simplechatbotproxy.chat.repository.entity.SessionHistory;
import com.simplechatbotproxy.chat.util.CommonUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private SessionHistoryRepository sessionHistoryRepository;

    @Test
    void welcome() throws Exception {
        mockMvc
                .perform(
                MockMvcRequestBuilders
                        .get("/chat/welcome")
                        .param("targetBot", "song-chat-service")
                )
                .andDo(print())
                .andExpect(header().exists("CHAT-SESSION-ID"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("text").exists())
                .andExpect(jsonPath("_links.query").exists());
    }

    @Test
    void chatQuery_BadRequest() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/chat/query")
                                .param("targetBot", "song-chat-service")
                                .param("queryText", "Hello World!")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void chatQuery() throws Exception {
        String sessionId = commonUtil.generateChatSessionId();
        String targetBot = "song-chat-service";

        SessionHistory sessionHistory =
                SessionHistory
                        .builder()
                        .chatSessionId(targetBot)
                        .botId(sessionId)
                        .build();

        sessionHistoryRepository.save(sessionHistory);

        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/chat/query")
                                .param("targetBot", targetBot)
                                .param("queryText", "Hello World!")
                                .param("chatSessionId", sessionId)
                )
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("text").exists())
                .andExpect(jsonPath("_links.query").exists());

        sessionHistoryRepository.delete(sessionHistory);
    }
}