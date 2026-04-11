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
