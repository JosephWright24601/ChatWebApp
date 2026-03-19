import { createRouter, createWebHistory } from 'vue-router'
import JoinView from "../views/JoinView.vue";
import ChatView from "../views/ChatView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "join",
      component: JoinView,
    },
    {
      path: "/chat",
      name: "chat",
      component: ChatView,
    },
  ],
});

export default router;