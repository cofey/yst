import type { PageQueryParams } from "@/shared/types/api";

export interface RoleItem {
  roleId: string;
  roleName: string;
  roleKey: string;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface RoleListQueryReq extends PageQueryParams {
  roleName?: string;
  roleKey?: string;
  status?: number;
}
