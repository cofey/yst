import { httpRequest } from "@/api/http";
import type { LoginRequest, LoginResponse, UserCreateReq, UserItem, UserUpdateReq } from "@/types";

export function loginApi(data: LoginRequest) {
  return httpRequest.post<LoginResponse>("/auth/login", data);
}

export function listUsersApi(keyword: string) {
  return httpRequest.get<UserItem[]>("/users", { keyword });
}

export function createUserApi(data: UserCreateReq) {
  return httpRequest.post<void>("/users", data);
}

export function updateUserApi(id: number, data: UserUpdateReq) {
  return httpRequest.put<void>(`/users/${id}`, data);
}

export function deleteUserApi(id: number) {
  return httpRequest.delete<void>(`/users/${id}`);
}
