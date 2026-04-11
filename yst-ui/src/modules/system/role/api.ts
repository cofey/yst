import { httpRequest } from "@/api/http";
import type { RoleItem, RoleListQueryReq } from "@/modules/system/role/types";
import type { PageResult } from "@/shared/types/api";

export function listRolesApi(params?: RoleListQueryReq) {
  return httpRequest.get<PageResult<RoleItem>>("/roles", {
    pageNum: 1,
    pageSize: 10,
    sortField: "create_time",
    sortOrder: "desc",
    ...params
  });
}

export function createRoleApi(data: {
  roleName: string;
  roleKey: string;
  status?: number;
  menuIds?: string[];
}) {
  return httpRequest.post<void>("/roles", data);
}

export function updateRoleApi(
  roleId: string,
  data: {
    roleName: string;
    roleKey: string;
    status?: number;
  }
) {
  return httpRequest.put<void>(`/roles/${roleId}`, data);
}

export function deleteRoleApi(roleId: string) {
  return httpRequest.delete<void>(`/roles/${roleId}`);
}

export function listRoleMenuIdsApi(roleId: string) {
  return httpRequest.get<string[]>(`/roles/${roleId}/menus`);
}

export function assignRoleMenusApi(roleId: string, menuIds: string[]) {
  return httpRequest.put<void>(`/roles/${roleId}/menus`, { menuIds });
}
