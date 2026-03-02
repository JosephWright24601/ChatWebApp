import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient: Client | null = null;

export function connectWebSocket(onMessage: (msg: any) => void) {
  const socket = new SockJS("http://localhost:8080/chat");

  stompClient = new Client({
    webSocketFactory: () => socket,

    onConnect: () => {
      console.log("Connected to WebSocket");

      stompClient?.subscribe("/topic/messages", (message) => {
        const parsed = JSON.parse(message.body);
        onMessage(parsed);
      });
    },

    onStompError: (frame) => {
      console.error("Broker error:", frame.headers["message"]);
    },
  });

  stompClient.activate();
}

export function sendMessage(message: any) {
  stompClient?.publish({
    destination: "/app/sendMessage",
    body: JSON.stringify(message),
  });
}

export function disconnectWebSocket() {
  stompClient?.deactivate();
}