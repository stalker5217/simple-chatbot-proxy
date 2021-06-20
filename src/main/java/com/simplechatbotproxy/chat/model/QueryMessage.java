package com.simplechatbotproxy.chat.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class QueryMessage {
    @NotEmpty
    private String targetBot;

    @NotEmpty
    private String queryText;

    @Pattern(regexp="^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    @NotEmpty
    private String chatSessionId;

    private String languageCode;
    private String event;
}
