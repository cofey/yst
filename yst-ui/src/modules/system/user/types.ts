import type { PageQueryParams } from "@/shared/types/api";

export interface UserItem {
  userId: string;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  companyIds: string[];
  companyNames: string[];
  roleIds: string[];
  roleNames: string[];
  status: number;
  createTime: string;
}

export interface UserListQueryReq extends PageQueryParams {
  username?: string;
  nickname?: string;
  phone?: string;
  companyId?: string;
  roleId?: string;
  status?: number;
}

export interface UserCreateReq {
  username: string;
  nickname: string;
  email?: string;
  phone?: string;
  companyIds?: string[];
  roleIds?: string[];
  status?: number;
  password?: string;
}

export interface UserUpdateReq {
  nickname: string;
  email?: string;
  phone?: string;
  companyIds?: string[];
  roleIds?: string[];
  status?: number;
  password?: string;
}
