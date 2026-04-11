import { defineStore } from "pinia";
import type { MenuTreeItem } from "@/modules/system/menu/types";

const USER_STORAGE_KEY = "user";

interface AuthState {
  token: string;
  userId: string;
  username: string;
  roles: string[];
  permissions: string[];
  menus: MenuTreeItem[];
}

interface AuthActions {
  setAuth(payload: {
    token: string;
    userId: string;
    username: string;
    roles?: string[];
    permissions?: string[];
  }): void;
  setAuthz(roles: string[], permissions: string[]): void;
  setMenus(menus: MenuTreeItem[]): void;
  hasMenuPath(path: string): boolean;
  hasPermi(permission: string): boolean;
  clearAuth(): void;
}

function readStorageState(): AuthState {
  const rawUser = localStorage.getItem(USER_STORAGE_KEY);
  if (rawUser) {
    try {
      const user = JSON.parse(rawUser);
      return {
        token: user?.token || "",
        userId: user?.userId || "",
        username: user?.username || "",
        roles: Array.isArray(user?.roles) ? user.roles : [],
        permissions: Array.isArray(user?.permissions) ? user.permissions : [],
        menus: Array.isArray(user?.menus) ? user.menus : []
      };
    } catch {
      // ignore broken storage and fallback to legacy keys
    }
  }
  return {
    token: localStorage.getItem("token") || "",
    userId: localStorage.getItem("userId") || "",
    username: localStorage.getItem("username") || "",
    roles: JSON.parse(localStorage.getItem("roles") || "[]"),
    permissions: JSON.parse(localStorage.getItem("permissions") || "[]"),
    menus: JSON.parse(localStorage.getItem("menus") || "[]")
  };
}

function persistStorageState(state: AuthState) {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(state));
}

export const useAuthStore = defineStore<"auth", AuthState, {}, AuthActions>("auth", {
  state: (): AuthState => readStorageState(),
  actions: {
    setAuth(payload: {
      token: string;
      userId: string;
      username: string;
      roles?: string[];
      permissions?: string[];
    }) {
      const roles = payload.roles || [];
      const permissions = payload.permissions || [];
      this.token = payload.token;
      this.userId = payload.userId;
      this.username = payload.username;
      this.roles = roles;
      this.permissions = permissions;
      persistStorageState(this.$state);
    },
    setAuthz(roles: string[], permissions: string[]) {
      this.roles = roles;
      this.permissions = permissions;
      persistStorageState(this.$state);
    },
    setMenus(menus: MenuTreeItem[]) {
      this.menus = menus || [];
      persistStorageState(this.$state);
    },
    hasMenuPath(path: string) {
      if (!path || path === "/home") {
        return true;
      }
      const stack = [...this.menus];
      while (stack.length) {
        const item = stack.pop()!;
        if (item.path && (item.path === path || path.startsWith(`${item.path}/`))) {
          return true;
        }
        if (item.children?.length) {
          stack.push(...item.children);
        }
      }
      return false;
    },
    hasPermi(permission: string) {
      return this.roles.includes("admin") || this.permissions.includes(permission);
    },
    clearAuth() {
      this.token = "";
      this.userId = "";
      this.username = "";
      this.roles = [];
      this.permissions = [];
      this.menus = [];
      localStorage.removeItem(USER_STORAGE_KEY);
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
      localStorage.removeItem("username");
      localStorage.removeItem("roles");
      localStorage.removeItem("permissions");
      localStorage.removeItem("menus");
    }
  }
});
