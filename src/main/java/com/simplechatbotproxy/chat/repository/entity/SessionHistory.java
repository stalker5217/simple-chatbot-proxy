package com.simplechatbotproxy.chat.repository.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class SessionHistory extends BaseTime {
    @Id
    private String chatSessionId;

    private String botId;

    @ManyToOne
    @JoinColumn(name="botId", insertable = false, updatable = false)
    private ChatbotInfo chatbotInfo;

    @Builder
    public SessionHistory(String chatSessionId, String botId){
        this.chatSessionId = chatSessionId;
        this.botId = botId;
    }
}
