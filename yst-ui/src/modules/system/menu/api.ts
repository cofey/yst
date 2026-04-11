import { httpRequest } from "@/api/http";
import type { MenuItem } from "@/modules/system/menu/types";

export function listMenusApi() {
  return httpRequest.get<MenuItem[]>("/menus");
}

export function createMenuApi(data: {
  parentId?: string | null;
  menuName: string;
  menuType: "M" | "C" | "F";
  path?: string;
  component?: string;
  icon?: string;
  perms?: string;
  visible?: number;
  status?: number;
  sort?: number;
}) {
  return httpRequest.post<void>("/menus", data);
}

export function updateMenuApi(
  menuId: string,
  data: {
    parentId?: string | null;
    menuName: string;
    menuType: "M" | "C" | "F";
    path?: string;
    component?: string;
    icon?: string;
    perms?: string;
    visible?: number;
    status?: number;
    sort?: number;
  }
) {
  return httpRequest.put<void>(`/menus/${menuId}`, data);
}

export function deleteMenuApi(menuId: string) {
  return httpRequest.delete<void>(`/menus/${menuId}`);
}

export function clearAllMenuAuthCacheApi() {
  return httpRequest.delete<void>("/menus/cache/all");
}
