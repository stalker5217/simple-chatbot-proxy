package com.simplechatbotproxy.chat.repository;

import com.simplechatbotproxy.chat.repository.entity.ConversationHistory;
import org.springframework.data.repository.CrudRepository;

public interface ConversationHistoryRepository extends CrudRepository<ConversationHistory, String> {
}
