export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
}

export interface UserItem {
  id: number;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  status: number;
  createTime: string;
}

export interface UserCreateReq {
  username: string;
  nickname: string;
  email?: string;
  phone?: string;
  status?: number;
  password?: string;
}

export interface UserUpdateReq {
  nickname: string;
  email?: string;
  phone?: string;
  status?: number;
  password?: string;
}
