<template>
  <router-view v-if="isLoginPage" />
  <div v-else class="app-shell">
    <header class="topbar">
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
          <el-menu-item index="route:/users">
            <el-icon>
              <HomeFilled />
            </el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="ai-coming">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            <span>AI对话</span>
          </el-menu-item>
          <el-sub-menu index="system" popper-class="top-menu-popper">
            <template #title>
              <el-icon>
                <Setting />
              </el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="route:/users">用户管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
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
import { ChatDotRound, HomeFilled, Setting } from "@element-plus/icons-vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isLoginPage = computed(() => route.path === "/login");
const activeMenu = computed(() => `route:${route.path}`);
const displayName = computed(() => authStore.username || "管理员");
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase());

const handleSelect = async (index: string) => {
  if (index.startsWith("route:")) {
    const targetPath = index.replace("route:", "");
    if (targetPath !== route.path) {
      await router.push(targetPath);
    }
    return;
  }
  if (index === "official-site") {
    window.open("https://gitee.com/y_project/RuoYi-Vue", "_blank");
    return;
  }
  ElMessage.info("该菜单正在建设中");
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
  align-items: center;
  justify-content: flex-start;
  padding: 0 18px;
  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}

.brand,
.topbar-center,
.topbar-right {
  display: flex;
  align-items: center;
  min-width: 0;
}

.brand {
  flex: 0 0 15%;
  max-width: 15%;
  gap: 12px;
  overflow: hidden;
}

.topbar-right {
  flex: 0 0 15%;
  max-width: 15%;
  justify-content: flex-end;
}

.topbar-center {
  flex: 1 1 70%;
  min-width: 70%;
  display: flex;
  align-items: center;
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
  font-size: 36px;
  transform: scale(0.46);
  transform-origin: left center;
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

:deep(.top-menu.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  padding-right: 10px;
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

:deep(.top-menu.el-menu--horizontal.is-active) {
  color: #409eff;
}

:deep(.top-menu.el-menu--horizontal:hover),
:deep(.top-menu.el-menu--horizontal > .el-sub-menu .el-sub-menu__title:hover) {
  color: #409eff;
  background: transparent;
}

:deep(.top-menu .el-sub-menu.is-active .el-sub-menu__title) {
  color: #409eff;
}

:deep(.top-menu-popper) {
  border-radius: 2px;
}

:deep(.top-menu-popper .el-menu) {
  min-width: 220px;
  border: none;
}

:deep(.top-menu-popper .el-menu-item),
:deep(.top-menu-popper .el-sub-menu__title) {
  font-size: 14px;
  height: 36px;
  line-height: 36px;
  padding: 0 10px;
  color: #3b4252;
}

:deep(.top-menu-popper .el-menu-item:hover),
:deep(.top-menu-popper .el-sub-menu__title:hover) {
  background: #f0f2f5;
  color: #111827;
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

@media (max-width: 1024px) {
  .topbar {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 12px;
    gap: 10px;
  }

  .brand,
  .topbar-right {
    width: auto;
  }

  .topbar-center {
    order: 3;
    width: 100%;
    justify-content: flex-start;
    overflow-x: auto;
    margin-left: 0;
  }

  .top-menu {
    min-width: 520px;
    width: auto;
  }
}
</style>
