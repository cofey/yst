import type { RouteRecordRaw } from "vue-router";

export const authRoutes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "login",
    component: () => import("@/modules/auth/login/pages/index.vue"),
    meta: {
      title: "登录",
      tab: false,
      cache: false
    }
  }
];
