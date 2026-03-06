package org.josephwright.chatwebapp.controller;

import org.josephwright.chatwebapp.model.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.josephwright.chatwebapp.repository.ChatMessageRepository;

@Controller
public class ChatController
{
    private final ChatMessageRepository repository;

    public ChatController(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(
            @DestinationVariable String roomId,
            ChatMessage message
    ) {
        message.setRoomId(roomId);
        repository.save(message);
        return message;
    }
}