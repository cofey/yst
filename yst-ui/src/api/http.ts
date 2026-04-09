import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router";
import { useAuthStore } from "@/stores/auth";
import type { AxiosRequestConfig } from "axios";
import type { ApiResponse } from "@/types";

const http = axios.create({
  baseURL: "/api",
  timeout: 15000
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(undefined, (error) => {
  if (error.response?.status === 401) {
    const authStore = useAuthStore();
    authStore.clearAuth();
    ElMessage.error("登录已失效，请重新登录");
    router.push("/login");
  } else {
    ElMessage.error(error.response?.data?.message || error.message || "网络异常");
  }
  return Promise.reject(error);
});

async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await http.request<ApiResponse<T>>(config);
  const body = response.data;
  if (body.code !== 200) {
    ElMessage.error(body.message || "请求失败");
    throw new Error(body.message || "请求失败");
  }
  return body.data;
}

export const httpRequest = {
  get<T>(url: string, params?: Record<string, unknown>) {
    return request<T>({ method: "GET", url, params });
  },
  post<T>(url: string, data?: unknown) {
    return request<T>({ method: "POST", url, data });
  },
  put<T>(url: string, data?: unknown) {
    return request<T>({ method: "PUT", url, data });
  },
  delete<T>(url: string) {
    return request<T>({ method: "DELETE", url });
  }
};

export default http;
