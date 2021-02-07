package com.simplechatbotproxy.chat.service;

import com.simplechatbotproxy.chat.model.QueryMessage;
import com.simplechatbotproxy.chat.model.ResultMessage;

public interface ChatService {
    /**
     * 챗봇 최초 진입 시 발생하는 Welcome 인사
     * @param queryMessage 챗봇 질의 메시지
     * @return ResultMessage - 챗봇 질의 결과
     */
    ResultMessage getWelcomeMessage(QueryMessage queryMessage);

    /**
     * 챗봇 질의 결과
     * @param queryMessage 챗봇 질의 메시지
     * @return ResultMessage - 챗봇 질의 결과
     */
    ResultMessage getQueryResultMessage(QueryMessage queryMessage);
}
