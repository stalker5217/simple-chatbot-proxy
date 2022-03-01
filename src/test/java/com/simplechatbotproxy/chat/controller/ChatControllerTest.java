package com.simplechatbotproxy.chat.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.simplechatbotproxy.chat.repository.SessionHistoryRepository;
import com.simplechatbotproxy.chat.repository.entity.SessionHistory;
import com.simplechatbotproxy.chat.util.CommonUtil;
import com.simplechatbotproxy.common.RestDocsConfiguration;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
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
                    RestDocumentationRequestBuilders
                        .get("/chat/welcome")
                        .param("targetBot", "song-chat-service")
                )
                .andDo(print())
                .andExpect(header().exists("CHAT-SESSION-ID"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("text").exists())
                .andExpect(jsonPath("_links.query").exists())
                .andDo(document(
                        "welcome",
                        requestParameters(
                                parameterWithName("targetBot").description("chatbot id want to talk")
                        ),
                        responseHeaders(
                                headerWithName("CHAT-SESSION-ID").description("session value for chat"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("response content type")
                        ),
                        responseFields(
                                fieldWithPath("text").description("answer of chatbot"),
                                fieldWithPath("_links.query.href").description("chat query link after welcome")
                        ),
                        links(
                                linkWithRel("query").description("chat query link after welcome")
                        ))
                );
    }

    @Test
    void chatQuery_BadRequest() throws Exception {
        mockMvc
                .perform(
                    RestDocumentationRequestBuilders
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
                    RestDocumentationRequestBuilders
                                .get("/chat/query")
                                .param("targetBot", targetBot)
                                .param("queryText", "Hello World!")
                                .param("chatSessionId", sessionId)
                )
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("text").exists())
                .andExpect(jsonPath("_links.query").exists())
                .andDo(document(
                        "query",
                        requestParameters(
                                parameterWithName("targetBot").description("chatbot id want to talk"),
                                parameterWithName("queryText").description("questions to ask the chatbot"),
                                parameterWithName("chatSessionId").description("id created by welcome api")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("response content type")
                        ),
                        responseFields(
                                fieldWithPath("text").description("answer of chatbot"),
                                fieldWithPath("_links.query.href").description("chat query link after welcome")
                        ),
                        links(
                                linkWithRel("query").description("chat query link after welcome")
                        ))
                );

        sessionHistoryRepository.delete(sessionHistory);
    }
}