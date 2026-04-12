import { httpRequest } from "@/api/http";
import type {
  UserCreateReq,
  UserItem,
  UserListQueryReq,
  UserUpdateReq
} from "@/modules/system/user/types";
import type { PageResult } from "@/shared/types/api";

export function listUsersApi(params?: UserListQueryReq) {
  return httpRequest.get<PageResult<UserItem>>("/users", {
    pageNum: 1,
    pageSize: 10,
    sortField: "create_time",
    sortOrder: "desc",
    ...params
  });
}

export function createUserApi(data: UserCreateReq) {
  return httpRequest.post<void>("/users", data);
}

export function updateUserApi(userId: string, data: UserUpdateReq) {
  return httpRequest.put<void>(`/users/${userId}`, data);
}

export function deleteUserApi(userId: string) {
  return httpRequest.delete<void>(`/users/${userId}`);
}

export function importUsersApi(file: File) {
  const formData = new FormData();
  formData.append("file", file);
  return httpRequest.post<void>("/users/import", formData);
}

export function exportUsersApi(filename = "用户列表.xlsx") {
  return httpRequest.download("/users/export", filename);
}
