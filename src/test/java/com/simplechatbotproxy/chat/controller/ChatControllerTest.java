package com.simplechatbotproxy.chat.controller;

import com.simplechatbotproxy.chat.repository.SessionHistoryRepository;
import com.simplechatbotproxy.chat.repository.entity.SessionHistory;
import com.simplechatbotproxy.chat.util.CommonUtil;
import com.simplechatbotproxy.common.RestDocsConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                MockMvcRequestBuilders
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