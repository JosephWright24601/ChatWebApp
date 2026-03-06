package org.josephwright.chatwebapp.repository;

import org.josephwright.chatwebapp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>
{
    List<ChatMessage> findByRoomId(String roomId);
}