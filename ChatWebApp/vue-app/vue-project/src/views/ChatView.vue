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
      </v-navigation-drawer>
      <v-main>
        <v-container>
          <h2># {{ currentRoom }}</h2>
          <div class="chat-box">
            <div v-for="(msg, index) in messages" :key="index">
              <strong>{{ msg.sender }}:</strong> {{ msg.content }}
            </div>
          </div>
          <div class="input-row">
            <v-text-field
              v-model="messageInput"
              label="Type a message"
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

const rooms = ["general", "gaming", "coding", "random"];
const currentRoom = ref("general");

const messages = ref<any[]>([]);
const messageInput = ref("");

function handleMessage(msg: any) {
  messages.value.push(msg);
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
  connectWebSocket(room, handleMessage);
}

function handleSend() {
  if (!messageInput.value) return;

  sendMessage({
    sender: "Joe",
    content: messageInput.value,
  });

  messageInput.value = "";
}

onMounted(async () => {
  await loadHistory(currentRoom.value);
  connectWebSocket(currentRoom.value, handleMessage);
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});
</script>

<style scoped>
.chat-box {
  border: 1px solid #ccc;
  height: 400px;
  overflow-y: auto;
  padding: 10px;
  margin-bottom: 10px;
  background: white;
}

.input-row {
  display: flex;
  gap: 10px;
}
</style>