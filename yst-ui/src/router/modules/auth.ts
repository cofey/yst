import type { RouteRecordRaw } from "vue-router";
import LoginPage from "@/modules/auth/login/pages/index.vue";

export const authRoutes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "login",
    component: LoginPage
  }
];
