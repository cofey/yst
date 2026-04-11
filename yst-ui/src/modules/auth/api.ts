import { httpRequest } from "@/api/http";
import type { AuthInfo, AuthMenu, LoginRequest, LoginResponse } from "@/modules/auth/types";

export function loginApi(data: LoginRequest) {
  return httpRequest.post<LoginResponse>("/auth/login", data);
}

export function authInfoApi() {
  return httpRequest.get<AuthInfo>("/auth/info");
}

export function authMenusApi() {
  return httpRequest.get<AuthMenu[]>("/auth/menus");
}
