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
    component: HomePage
  },
  {
    path: "/system/users",
    name: "users",
    component: UserPage
  },
  {
    path: "/system/roles",
    name: "roles",
    component: RolePage
  },
  {
    path: "/system/menus",
    name: "menus",
    component: MenuPage
  },
  {
    path: "/system/companies",
    name: "companies",
    component: CompanyPage
  },
  {
    path: "/system/dicts",
    name: "dicts",
    component: DictPage
  },
  {
    path: "/system/dicts/:dictType/data",
    name: "dict-data",
    component: DictDataPage
  }
];
