package org.josephwright.chatwebapp.websocket;

import org.josephwright.chatwebapp.service.OnlineUserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(OnlineUserService onlineUserService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.onlineUserService = onlineUserService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        String username = "User-" + event.getMessage().getHeaders().get("simpSessionId");
        onlineUserService.addUser(username);

        messagingTemplate.convertAndSend(
                "/topic/users",
                onlineUserService.getOnlineUsers()
        );
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String username = "User-" + event.getSessionId();
        onlineUserService.removeUser(username);

        messagingTemplate.convertAndSend(
                "/topic/users",
                onlineUserService.getOnlineUsers()
        );
    }
}