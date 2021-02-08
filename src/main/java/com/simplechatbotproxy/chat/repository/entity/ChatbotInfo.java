package com.simplechatbotproxy.chat.repository.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class ChatbotInfo {
    @Id
    private String botId;

    private String languageCode;
}
