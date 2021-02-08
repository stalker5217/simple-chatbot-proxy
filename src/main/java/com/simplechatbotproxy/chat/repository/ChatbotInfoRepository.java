package com.simplechatbotproxy.chat.repository;

import com.simplechatbotproxy.chat.repository.entity.ChatbotInfo;
import org.springframework.data.repository.CrudRepository;

public interface ChatbotInfoRepository extends CrudRepository<ChatbotInfo, String> {
}
