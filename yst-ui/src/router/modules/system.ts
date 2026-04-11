import type { RouteRecordRaw } from "vue-router";
import HomePage from "@/modules/home/pages/index.vue";
import UserPage from "@/modules/system/user/pages/index.vue";
import RolePage from "@/modules/system/role/pages/index.vue";
import MenuPage from "@/modules/system/menu/pages/index.vue";
import CompanyPage from "@/modules/system/company/pages/index.vue";
import DictPage from "@/modules/system/dict/pages/index.vue";
import DictDataPage from "@/modules/system/dict/pages/data.vue";

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: "/home",
    name: "home",
    component: HomePage,
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
    component: UserPage,
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
    component: RolePage,
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
    component: MenuPage,
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
    component: CompanyPage,
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
    component: DictPage,
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
    component: DictDataPage,
    meta: {
      title: "字典数据",
      cache: true,
      tab: true,
      keepAliveName: "SystemDictDataPage"
    }
  }
];
