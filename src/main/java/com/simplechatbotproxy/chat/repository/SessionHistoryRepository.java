package com.simplechatbotproxy.chat.repository;

import com.simplechatbotproxy.chat.repository.entity.SessionHistory;
import org.springframework.data.repository.CrudRepository;

public interface SessionHistoryRepository extends CrudRepository<SessionHistory, String> {
}
