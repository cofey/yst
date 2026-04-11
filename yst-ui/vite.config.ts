import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const proxyTarget = env.VITE_API_PROXY_TARGET;

  if (!proxyTarget) {
    throw new Error(
      `[vite] 缺少环境变量 VITE_API_PROXY_TARGET，请在 .env.${mode} 中配置后再启动。`
    );
  }

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url))
      }
    },
    server: {
      port: 5173,
      proxy: {
        "/api": {
          target: proxyTarget,
          changeOrigin: true
        }
      }
    }
  };
});
