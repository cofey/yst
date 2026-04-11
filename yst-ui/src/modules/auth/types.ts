import type { MenuTreeItem } from "@/modules/system/menu/types";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  userId: string;
  token: string;
  username: string;
}

export interface AuthInfo {
  userId: string;
  username: string;
  roles: string[];
  permissions: string[];
}

export type AuthMenu = MenuTreeItem;
