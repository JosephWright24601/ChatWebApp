import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";

let stompClient: Client | null = null;
let currentRoomId: string = "general";

export function connectWebSocket(
  roomId: string,
  onMessage: (msg: any) => void
) {
  currentRoomId = roomId;

  const socket = new SockJS("http://localhost:8080/chat");

  stompClient = new Client({
    webSocketFactory: () => socket,

    onConnect: () => {
      console.log("Connected to WebSocket");

      stompClient?.subscribe(`/topic/room/${currentRoomId}`, (message) => {
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
  if (!stompClient || !stompClient.connected) {
    console.error("STOMP client not connected");
    return;
  }

  stompClient.publish({
    destination: `/app/chat/${currentRoomId}`,
    body: JSON.stringify(message),
  });
}

export function disconnectWebSocket() {
  stompClient?.deactivate();
}