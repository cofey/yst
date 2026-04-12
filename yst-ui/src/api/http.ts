import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router";
import { useAuthStore } from "@/stores/auth";
import type { AxiosRequestConfig, AxiosResponse } from "axios";
import type { ApiResponse } from "@/shared/types/api";

const http = axios.create({
  baseURL: "/api",
  timeout: 15000
});

http.interceptors.request.use((config) => {
  let token = "";
  const rawUser = localStorage.getItem("user");
  if (rawUser) {
    try {
      token = JSON.parse(rawUser)?.token || "";
    } catch {
      token = "";
    }
  }
  if (!token) {
    token = localStorage.getItem("token") || "";
  }
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
  } else if (error.response?.status === 403) {
    ElMessage.error(error.response?.data?.message || "无权限访问");
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

function parseFilenameFromHeaders(response: AxiosResponse<Blob>, fallback: string) {
  const contentDisposition = response.headers["content-disposition"];
  if (!contentDisposition) {
    return fallback;
  }
  const utfMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
  if (utfMatch?.[1]) {
    try {
      return decodeURIComponent(utfMatch[1]);
    } catch {
      return utfMatch[1];
    }
  }
  const normalMatch = contentDisposition.match(/filename="?([^"]+)"?/i);
  return normalMatch?.[1] || fallback;
}

function triggerDownload(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  window.URL.revokeObjectURL(url);
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
  },
  async download(url: string, filename = "download.xlsx", params?: Record<string, unknown>) {
    const response = await http.request<Blob>({
      method: "GET",
      url,
      params,
      responseType: "blob"
    });
    const downloadFilename = parseFilenameFromHeaders(response, filename);
    const blob = response.data instanceof Blob ? response.data : new Blob([response.data]);
    triggerDownload(blob, downloadFilename);
  }
};

export default http;
