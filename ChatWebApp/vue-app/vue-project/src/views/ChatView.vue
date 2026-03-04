<template>
  <div style="padding: 20px;">
    <h2>Live Chat Test</h2>

    <div style="border: 1px solid #ccc; height: 200px; overflow-y: auto; margin-bottom: 10px;">
      <div v-for="(msg, index) in messages" :key="index">
        <strong>{{ msg.sender }}:</strong> {{ msg.content }}
      </div>
    </div>

    <input v-model="messageInput" placeholder="Type a message..." />
    <button @click="handleSend">Send</button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";
import { connectWebSocket, sendMessage, disconnectWebSocket } from "@/services/websocket";

const messages = ref<any[]>([]);
const messageInput = ref("");

onMounted(() => {
  connectWebSocket("general", (msg) => {
    messages.value.push(msg);
  });
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});

function handleSend() {
  if (!messageInput.value) return;

  sendMessage({
    sender: "Joe",
    content: messageInput.value,
  });

  messageInput.value = "";
}
</script>