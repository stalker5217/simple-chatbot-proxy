package com.simplechatbotproxy.chat.model;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class QueryMessage {
    private String targetBot;
    private String queryText;
    private String event;

    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    private String chatSessionId;
}
