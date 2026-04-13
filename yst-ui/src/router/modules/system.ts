import type { RouteRecordRaw } from "vue-router";

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: "/home",
    name: "home",
    component: () => import("@/modules/home/pages/index.vue"),
    meta: {
      title: "首页",
      affix: true,
      cache: true,
      tab: true,
      keepAliveName: "HomePage"
    }
  },
  {
    path: "/system/users",
    name: "users",
    component: () => import("@/modules/system/user/pages/index.vue"),
    meta: {
      title: "用户管理",
      cache: true,
      tab: true,
      keepAliveName: "SystemUserPage"
    }
  },
  {
    path: "/system/roles",
    name: "roles",
    component: () => import("@/modules/system/role/pages/index.vue"),
    meta: {
      title: "角色管理",
      cache: true,
      tab: true,
      keepAliveName: "SystemRolePage"
    }
  },
  {
    path: "/system/menus",
    name: "menus",
    component: () => import("@/modules/system/menu/pages/index.vue"),
    meta: {
      title: "菜单管理",
      cache: true,
      tab: true,
      keepAliveName: "SystemMenuPage"
    }
  },
  {
    path: "/system/companies",
    name: "companies",
    component: () => import("@/modules/system/company/pages/index.vue"),
    meta: {
      title: "单位管理",
      cache: true,
      tab: true,
      keepAliveName: "SystemCompanyPage"
    }
  },
  {
    path: "/system/dicts",
    name: "dicts",
    component: () => import("@/modules/system/dict/pages/index.vue"),
    meta: {
      title: "字典管理",
      cache: true,
      tab: true,
      keepAliveName: "SystemDictTypePage"
    }
  },
  {
    path: "/system/dicts/:dictType/data",
    name: "dict-data",
    component: () => import("@/modules/system/dict/pages/data.vue"),
    meta: {
      title: "字典数据",
      cache: true,
      tab: true,
      keepAliveName: "SystemDictDataPage"
    }
  }
];
