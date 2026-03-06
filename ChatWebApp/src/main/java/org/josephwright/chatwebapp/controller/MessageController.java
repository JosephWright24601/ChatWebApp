package org.josephwright.chatwebapp.controller;

import org.josephwright.chatwebapp.model.ChatMessage;
import org.josephwright.chatwebapp.repository.ChatMessageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
//@CrossOrigin(origins = "http://localhost:5173")
public class MessageController {

    private final ChatMessageRepository repository;

    public MessageController(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        return repository.findByRoomId(roomId);
    }
}