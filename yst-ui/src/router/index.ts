import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";
import { useAuthStore } from "@/stores/auth";
import { authRoutes } from "@/router/modules/auth";
import { systemRoutes } from "@/router/modules/system";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...authRoutes,
    {
      path: "/",
      redirect: "/home"
    },
    ...systemRoutes
  ]
});

let lastWarnedNoPermPath = "";

router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (to.path !== "/login" && !authStore.token) {
    return "/login";
  }
  if (to.path === "/login" && authStore.token) {
    return "/home";
  }
  if (to.path !== "/login" && !authStore.hasMenuPath(to.path)) {
    if (lastWarnedNoPermPath !== to.path) {
      ElMessage.warning("当前访问的页面不在你的权限内");
      lastWarnedNoPermPath = to.path;
    }
  } else {
    lastWarnedNoPermPath = "";
  }
  return true;
});

export default router;
