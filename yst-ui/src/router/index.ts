import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import LoginView from "@/views/LoginView.vue";
import UserView from "@/views/UserView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "login",
      component: LoginView
    },
    {
      path: "/",
      redirect: "/users"
    },
    {
      path: "/users",
      name: "users",
      component: UserView
    }
  ]
});

router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (to.path !== "/login" && !authStore.token) {
    return "/login";
  }
  if (to.path === "/login" && authStore.token) {
    return "/users";
  }
  return true;
});

export default router;
