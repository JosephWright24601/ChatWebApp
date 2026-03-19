package org.josephwright.chatwebapp.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserService {

    private final Map<String, Integer> userSessions = new ConcurrentHashMap<>();

    public void addUser(String username) {
        userSessions.merge(username, 1, Integer::sum);
    }

    public void removeUser(String username) {
        userSessions.computeIfPresent(username, (key, count) -> {
            if (count <= 1) return null;
            return count - 1;
        });
    }

    public Set<String> getOnlineUsers() {
        return userSessions.keySet();
    }
}