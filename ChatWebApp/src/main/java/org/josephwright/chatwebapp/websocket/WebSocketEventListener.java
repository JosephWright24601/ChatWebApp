package org.josephwright.chatwebapp.websocket;

import org.josephwright.chatwebapp.service.OnlineUserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

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
    public void handleWebSocketConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getFirstNativeHeader("username");

        if (username == null) {
            username = "Anonymous";
        }

        accessor.getSessionAttributes().put("username", username);
        onlineUserService.addUser(username);

        messagingTemplate.convertAndSend(
                "/topic/users",
                onlineUserService.getOnlineUsers()
        );
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) accessor.getSessionAttributes().get("username");

        if (username != null) {
            onlineUserService.removeUser(username);
        }

        messagingTemplate.convertAndSend(
                "/topic/users",
                onlineUserService.getOnlineUsers()
        );
    }
}