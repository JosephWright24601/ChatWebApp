<template>
  <v-app>
    <v-container class="fill-height d-flex align-center justify-center">
      <v-card width="400" class="pa-6">
        <h2 class="mb-4">Join Chat</h2>

        <v-text-field
          v-model="username"
          label="Enter username"
          @keyup.enter="joinChat"
        />

        <v-btn block color="primary" @click="joinChat">
          Join
        </v-btn>
      </v-card>
    </v-container>
  </v-app>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const username = ref("");
const router = useRouter();
const userStore = useUserStore();

function joinChat() {
  if (!username.value.trim()) return;

  userStore.username = username.value.trim();
  router.push("/chat");
}
</script>