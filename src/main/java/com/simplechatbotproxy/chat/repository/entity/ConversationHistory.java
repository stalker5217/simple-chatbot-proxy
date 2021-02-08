package com.simplechatbotproxy.chat.repository.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class ConversationHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String chatSessionId;

    @ManyToOne
    @JoinColumn(name="chatSessionId", insertable = false, updatable = false)
    private SessionHistory sessionHistory;

    @Column(length=512)
    private String queryText;

    private String event;

    @Column(length=512)
    private String responseText;

    private String intentName;

    @Column(length=1)
    private boolean fallbackFlag;

    private double intentDetectionConfidence;

    @Builder
    public ConversationHistory(String chatSessionId,
                               String queryText,
                               String event,
                               String responseText,
                               String intentName,
                               boolean fallbackFlag,
                               double intentDetectionConfidence)
    {
        this.chatSessionId = chatSessionId;
        this.queryText = queryText;
        this.event = event;
        this.responseText = responseText;
        this.intentName = intentName;
        this.fallbackFlag = fallbackFlag;
        this.intentDetectionConfidence = intentDetectionConfidence;
    }
}
