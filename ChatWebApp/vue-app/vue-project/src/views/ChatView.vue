<template>
  <v-app>
    <v-layout>
      <v-navigation-drawer permanent width="220">
        <v-list density="compact" nav>
          <v-list-subheader>Rooms</v-list-subheader>
          <v-list-item
            v-for="room in rooms"
            :key="room"
            :active="room === currentRoom"
            @click="switchRoom(room)"
          >
            <v-list-item-title># {{ room }}</v-list-item-title>
          </v-list-item>
        </v-list>
        <v-divider class="my-3"></v-divider>
        <v-list-subheader>Online</v-list-subheader>
        <v-list-item
          v-for="user in onlineUsers"
          :key="user"
        >
          <v-list-item-title>
            🟢 {{ user }}
          </v-list-item-title>
        </v-list-item>
      </v-navigation-drawer>
      <v-main>
        <v-container>
          <h2># {{ currentRoom }}</h2>
          <div class="chat-box">
            <transition-group name="chat">
              <div
                v-for="(msg, index) in messages"
                :key="index"
                class="message"
              >
                <div class="bubble">
                  <div class="message-header">
                    <strong>{{ msg.sender }}</strong>
                    <span class="time">{{ formatTime(msg.timestamp) }}</span>
                  </div>
                  <div>{{ msg.content }}</div>
                </div>
              </div>
            </transition-group>
            <div v-if="typingUser" class="typing-indicator">
              {{ typingUser }} is typing...
            </div>
          </div>
          <div class="input-row">
            <v-text-field
              v-model="messageInput"
              label="Type a message"
              @input="sendTyping"
              @keyup.enter="handleSend"
              hide-details
            />
            <v-btn color="primary" @click="handleSend">Send</v-btn>
          </div>
        </v-container>
      </v-main>
    </v-layout>
  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";
import { connectWebSocket, sendMessage, disconnectWebSocket } from "@/services/websocket";
import { nextTick } from "vue";
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";

const rooms = ["general", "gaming", "coding", "random"];
const currentRoom = ref("general");
const userStore = useUserStore();
const router = useRouter();

const messages = ref<any[]>([]);
const messageInput = ref("");
const typingUser = ref<string | null>(null);
const onlineUsers = ref<string[]>([]);

let typingTimeout: any;

function handleMessage(msg: any) {
  if (!msg) 
    return;

  if (msg.typing) {
    typingUser.value = msg.sender;

    setTimeout(() => {
      typingUser.value = null
    }, 2000)

    return;
  }

  if (!msg.content) 
    return;

  messages.value.push(msg);

  nextTick(() => { //TODO: Update to show a button that a new message has arrived if the user is scrolled up
    const box = document.querySelector(".chat-box");
    if (box) 
      box.scrollTop = box.scrollHeight;
  })
}

async function loadHistory(room: string) {
  const response = await fetch(`http://localhost:8080/messages/${room}`);
  messages.value = await response.json();
}

async function switchRoom(room: string) {
  if (room === currentRoom.value) return;

  disconnectWebSocket();
  currentRoom.value = room;
  messages.value = [];

  await loadHistory(room);
  connectWebSocket(
    room, 
    userStore.username,
    handleMessage, 
    (users) => {
      onlineUsers.value = users
    }
  );
}

function handleSend() {
  if (!messageInput.value) return;

  sendMessage({
    sender: userStore.username,
    content: messageInput.value,
  });

  messageInput.value = "";
}

function formatTime(timestamp: string) {
  const date = new Date(timestamp);
  return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

function sendTyping() {
  clearTimeout(typingTimeout);
  sendMessage({
    sender: userStore.username,
    typing: true
  });

  typingTimeout = setTimeout(() => {}, 1500);
}

onMounted(async () => {
  if (!userStore.username) {
    router.push("/");
    return;
  }

  await loadHistory(currentRoom.value);
  connectWebSocket(
    currentRoom.value, 
    userStore.username,
    handleMessage, 
    (users) => {
      onlineUsers.value = users
    }
  );
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});
</script>

<style scoped>
.chat-box {
  height: 420px;
  overflow-y: auto;
  padding: 15px;
  background: #1e1e1e;
  border-radius: 10px;
  margin-bottom: 12px;
}

.message {
  display: flex;
  margin-bottom: 10px;
}

.bubble {
  background: #2f3136;
  padding: 10px 14px;
  border-radius: 16px;
  max-width: 60%;
  color: white;
  box-shadow: 0 2px 5px rgba(0,0,0,0.3);
}

.bubble strong {
  font-size: 0.9rem;
  color: #7289da;
}

.chat-enter-active {
  transition: all 0.25s ease;
}

.chat-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.chat-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.message-header {
  display: flex;
  gap: 8px;
  align-items: center;
}

.time {
  font-size: 0.75rem;
  color: #9ca3af;
}

.typing-indicator {
  font-size: 0.85rem;
  color: #9ca3af;
  margin-bottom: 8px;
  font-style: italic;
}
</style>