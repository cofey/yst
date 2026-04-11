<template>
  <router-view v-if="isLoginPage" />
  <div v-else class="app-shell">
    <header class="topbar">
      <div class="topbar-left">
        <div class="brand">
          <span class="brand-logo"></span>
          <span class="brand-text">YST 管理系统</span>
        </div>
        <div class="topbar-center">
          <el-menu
            mode="horizontal"
            class="top-menu"
            :default-active="activeMenu"
            @select="handleSelect"
          >
            <TopNavMenuNode
              v-for="menu in authStore.menus"
              :key="menu.menuId"
              :node="menu"
            />
          </el-menu>
        </div>
      </div>
      <div class="topbar-right">
        <el-dropdown trigger="click">
          <div class="user-info">
            <el-avatar :size="32">{{ avatarText }}</el-avatar>
            <span class="username">{{ displayName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import type { AuthMenu } from "@/modules/auth/types";
import TopNavMenuNode from "@/components/layout/TopNavMenuNode.vue";
import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isLoginPage = computed(() => route.path === "/login");
const collectMenuPaths = (menus: AuthMenu[]): string[] => {
  const paths: string[] = [];
  const stack = [...menus];
  while (stack.length) {
    const menu = stack.pop()!;
    if (menu.path) {
      paths.push(menu.path);
    }
    if (menu.children?.length) {
      stack.push(...menu.children);
    }
  }
  return paths;
};
const menuPaths = computed(() => collectMenuPaths(authStore.menus));
const activeMenu = computed(() => {
  const hit = menuPaths.value
    .filter((path) => route.path === path || route.path.startsWith(`${path}/`))
    .sort((a, b) => b.length - a.length)[0];
  return hit ? `route:${hit}` : "";
});
const displayName = computed(() => authStore.username || "管理员");
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase());

const handleSelect = async (index: string) => {
  if (!index.startsWith("route:")) {
    return;
  }
  const targetPath = index.replace("route:", "");
  if (targetPath !== route.path) {
    try {
      await router.push(targetPath);
    } catch {
      ElMessage.warning("菜单已展示，但路由未配置");
    }
  }
};

const logout = async () => {
  authStore.clearAuth();
  await router.push("/login");
};
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f4f6fb;
}

.topbar {
  height: 52px;
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 0 18px;
  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}

.topbar-left,
.brand,
.topbar-center,
.topbar-right {
  display: flex;
  align-items: center;
  min-width: 0;
}

.topbar-left {
  flex: 1 1 auto;
  gap: 8px;
  overflow: hidden;
}

.brand {
  flex: 0 1 auto;
  gap: 12px;
  overflow: hidden;
}

.topbar-right {
  flex: 0 0 15%;
  max-width: 15%;
  justify-content: flex-end;
}

.topbar-center {
  flex: 1 1 auto;
  min-width: 0;
  justify-content: flex-start;
  overflow: hidden;
}

.brand-logo {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: #409eff;
}

.brand-text {
  font-size: 16px;
  height: 20px;
  line-height: 20px;
  font-weight: 700;
  color: #11253d;
  white-space: nowrap;
}

.top-menu {
  height: 100%;
  border-bottom: none;
  width: 100%;
  display: flex;
  align-items: center;
}

:deep(.top-menu.el-menu--horizontal) {
  --el-menu-horizontal-height: 52px;
  height: 100%;
  background-color: transparent;
  padding-left: 0;
}

:deep(.top-menu.el-menu--horizontal),
:deep(.top-menu.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  height: 52px;
  line-height: 48px;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  padding: 0 10px;
  border-bottom: 2px solid transparent;
}

:deep(.top-menu .el-sub-menu__icon-arrow) {
  position: static;
  right: auto;
  top: auto;
  margin-left: 6px;
  margin-top: 2px;
}

:deep(.top-menu .el-icon) {
  margin-right: 6px;
  font-size: 16px;
}

:deep(.top-menu-popper.el-popper),
:deep(.top-menu-popper.el-popper.is-light) {
  border: none !important;
  outline: none !important;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgb(17 37 61 / 10%);
  padding: 6px;
}

:deep(.top-menu-popper .el-popper__arrow::before) {
  border: none !important;
  box-shadow: none !important;
}

:deep(.top-menu-popper .el-menu) {
  border: none;
  background: transparent;
  --el-menu-hover-bg-color: #f5f7fa;
  --el-menu-hover-text-color: #1f2937;
  --el-menu-active-color: #1f2937;
}

:deep(.top-menu-popper .el-menu-item) {
  border-radius: 8px;
  margin: 2px 0;
  border: none !important;
  outline: none !important;
}

:deep(.top-menu-popper .el-menu-item:hover),
:deep(.top-menu-popper .el-menu-item:focus),
:deep(.top-menu-popper .el-menu-item.is-active) {
  border: none !important;
  color: #1f2937 !important;
  background: #f5f7fa !important;
}

:deep(.top-menu.el-menu--horizontal > .el-menu-item.is-active),
:deep(.top-menu.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title) {
  color: #409eff;
  border-bottom-color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.app-main {
  padding-top: 0;
}
</style>

<style>
.top-menu-popper.el-popper,
.top-menu-popper.el-popper.is-light {
  border: none !important;
  outline: none !important;
  box-shadow: 0 6px 18px rgb(17 37 61 / 10%) !important;
}

.top-menu-popper .el-popper__arrow::before {
  border: none !important;
  box-shadow: none !important;
}

.top-menu-popper .el-menu {
  border: none !important;
  background: transparent !important;
  --el-menu-hover-bg-color: #f5f7fa;
  --el-menu-hover-text-color: #1f2937;
  --el-menu-active-color: #1f2937;
}

.top-menu-popper .el-menu-item {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  color: #1f2937 !important;
  background: transparent !important;
  border-radius: 8px;
  margin: 2px 0;
}

.top-menu-popper .el-menu-item .el-icon {
  font-size: 14px;
  margin-right: 4px;
}

.top-menu-popper .el-menu-item:hover,
.top-menu-popper .el-menu-item:focus,
.top-menu-popper .el-menu-item:focus-visible {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  color: #1f2937 !important;
  background: #f5f7fa !important;
}

.top-menu-popper .el-menu-item.is-active {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  color: #1677ff !important;
  background: #ecf5ff !important;
}
</style>
