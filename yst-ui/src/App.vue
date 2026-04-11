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
            <el-avatar :size="29">{{ avatarText }}</el-avatar>
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
    <section class="tabbar">
      <el-button
        class="tab-switch-btn"
        :disabled="!canSwitchPrev"
        circle
        @click="switchTabByStep(-1)"
      >
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <div class="tabs-track">
        <el-tabs
          :model-value="tabsStore.activeTabKey"
          type="card"
          class="app-tabs"
          @tab-change="handleTabChange"
          @tab-remove="handleTabRemove"
        >
          <el-tab-pane
            v-for="tab in tabsStore.visitedTabs"
            :key="tab.key"
            :label="tab.title"
            :name="tab.key"
            :closable="tab.closable && tabsStore.visitedTabs.length > 1"
          />
        </el-tabs>
      </div>
      <el-button
        class="tab-switch-btn"
        :disabled="!canSwitchNext"
        circle
        @click="switchTabByStep(1)"
      >
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </section>
    <main class="app-main">
      <router-view v-slot="{ Component, route: viewRoute }">
        <KeepAlive :include="tabsStore.cachedViewKeys">
          <component
            :is="Component"
            v-if="Component && shouldCache(viewRoute)"
            :key="buildTabKey(viewRoute)"
          />
        </KeepAlive>
        <component
          :is="Component"
          v-if="Component && !shouldCache(viewRoute)"
          :key="buildTabKey(viewRoute)"
        />
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { ArrowLeft, ArrowRight } from "@element-plus/icons-vue";
import { useRoute, useRouter, type RouteLocationNormalizedLoaded } from "vue-router";
import type { AuthMenu } from "@/modules/auth/types";
import TopNavMenuNode from "@/components/layout/TopNavMenuNode.vue";
import { useAuthStore } from "@/stores/auth";
import { buildTabKey, useTabsStore } from "@/stores/tabs";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const tabsStore = useTabsStore();

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
const activeTabIndex = computed(() =>
  tabsStore.visitedTabs.findIndex((tab) => tab.key === tabsStore.activeTabKey)
);
const canSwitchPrev = computed(
  () => tabsStore.visitedTabs.length > 1 && activeTabIndex.value > 0
);
const canSwitchNext = computed(
  () =>
    tabsStore.visitedTabs.length > 1 && activeTabIndex.value >= 0 &&
    activeTabIndex.value < tabsStore.visitedTabs.length - 1
);

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

const shouldTrackRoute = (targetRoute: RouteLocationNormalizedLoaded) => {
  if (targetRoute.path === "/login") {
    return false;
  }
  if (!targetRoute.name) {
    return false;
  }
  if (!authStore.hasMenuPath(targetRoute.path)) {
    return false;
  }
  return targetRoute.meta.tab !== false;
};

const shouldCache = (targetRoute: RouteLocationNormalizedLoaded) =>
  shouldTrackRoute(targetRoute) && targetRoute.meta.cache === true;

const syncRouteToTabs = (targetRoute: RouteLocationNormalizedLoaded) => {
  if (!shouldTrackRoute(targetRoute)) {
    return;
  }
  tabsStore.openTab(targetRoute);
};

const resolveStartupPath = () => {
  const activePath = tabsStore.getActivePath();
  if (!activePath) {
    return "";
  }
  const resolved = router.resolve(activePath);
  if (!resolved.matched.length) {
    return "/home";
  }
  if (!authStore.hasMenuPath(resolved.path)) {
    return "/home";
  }
  return activePath;
};

const handleTabChange = async (name: string | number) => {
  const key = String(name);
  const tab = tabsStore.getTabByKey(key);
  if (!tab) {
    return;
  }
  tabsStore.setActiveTab(key);
  if (route.fullPath !== tab.fullPath) {
    await router.push(tab.fullPath);
  }
};

const handleTabRemove = async (name: string | number) => {
  const nextPath = tabsStore.closeTab(String(name));
  if (nextPath && nextPath !== route.fullPath) {
    await router.push(nextPath);
  }
};

const switchTabByStep = async (step: -1 | 1) => {
  if (tabsStore.visitedTabs.length < 2) {
    return;
  }
  const currentIndex = activeTabIndex.value >= 0 ? activeTabIndex.value : 0;
  const nextIndex = currentIndex + step;
  if (nextIndex < 0 || nextIndex >= tabsStore.visitedTabs.length) {
    return;
  }
  const target = tabsStore.visitedTabs[nextIndex];
  tabsStore.setActiveTab(target.key);
  if (target.fullPath !== route.fullPath) {
    await router.push(target.fullPath);
  }
};

const logout = async () => {
  tabsStore.reset();
  authStore.clearAuth();
  await router.push("/login");
};

onMounted(async () => {
  tabsStore.restore();
  if (route.path === "/login") {
    return;
  }
  const startupPath = resolveStartupPath();
  if (startupPath && startupPath !== route.fullPath) {
    await router.replace(startupPath);
    return;
  }
  syncRouteToTabs(route);
});

watch(
  () => route.fullPath,
  () => {
    if (route.path === "/login") {
      return;
    }
    syncRouteToTabs(route);
  },
  {
    immediate: true
  }
);
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f2f4f7;
}

.topbar {
  height: 54px;
  display: flex;
  gap: 2px;
  align-items: center;
  padding: 2px 8px;
  background: linear-gradient(120deg, #4971d6 0%, #3f68d2 100%);
  border-bottom: 1px solid #3158bf;
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
  gap: 4px;
  overflow: hidden;
}

.brand {
  flex: 0 0 auto;
  gap: 2px;
  overflow: hidden;
}

.topbar-right {
  flex: 0 0 12%;
  max-width: 12%;
  justify-content: flex-end;
}

.topbar-center {
  flex: 1 1 auto;
  min-width: 0;
  justify-content: flex-start;
  overflow: hidden;
}

.brand-logo {
  width: 27px;
  height: 27px;
  border-radius: 10px;
  border: 2px solid rgb(255 255 255 / 75%);
  position: relative;
}

.brand-logo::before {
  content: "";
  position: absolute;
  inset: 6px 5px 8px;
  border: 2px solid rgb(255 255 255 / 75%);
  border-top: none;
  border-radius: 3px;
}

.brand-logo::after {
  content: "";
  position: absolute;
  left: 5px;
  right: 5px;
  top: 5px;
  height: 6px;
  border: 2px solid rgb(255 255 255 / 75%);
  border-bottom: none;
  transform: skewY(-22deg);
  border-radius: 2px;
}

.brand-text {
  font-size: 13px;
  height: 13px;
  line-height: 13px;
  font-weight: 700;
  color: rgb(255 255 255 / 96%);
  white-space: nowrap;
  margin: 0;
  padding: 0;
}

.top-menu {
  height: 100%;
  border-bottom: none;
  width: 100%;
  display: flex;
  align-items: center;
}

:deep(.top-menu.el-menu--horizontal) {
  --el-menu-horizontal-height: 54px;
  height: 100%;
  background-color: transparent;
  padding-left: 0;
}

:deep(.top-menu.el-menu--horizontal),
:deep(.top-menu.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  height: 54px;
  line-height: 54px;
  font-size: 13px;
  font-weight: 500;
  color: rgb(255 255 255 / 90%);
  padding: 0 12px;
  border-bottom: 2px solid transparent !important;
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
  font-size: 13px;
  color: rgb(255 255 255 / 90%);
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
  color: #ffffff !important;
  border-bottom-color: #9ec0ff !important;
  background: rgb(255 255 255 / 10%);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 2px;
  border-radius: 14px;
}

.user-info:hover {
  background: rgb(255 255 255 / 12%);
}

.username {
  color: rgb(255 255 255 / 92%);
  font-size: 13px;
  font-weight: 400;
}

.app-main {
  padding: 0;
}

.tabbar {
  display: flex;
  align-items: center;
  gap: 2px;
  height: 46px;
  padding: 0 8px;
  background: #eceef2;
  border-bottom: 1px solid #d4d8de;
}

.tabs-track {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.app-tabs {
  width: 100%;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header) {
  margin-bottom: 0;
  border-bottom: none;
}

.tab-switch-btn {
  flex: 0 0 auto;
  width: 26px;
  height: 26px;
  padding: 0;
  color: #9aa3af;
  border: none;
  background: transparent;
  border-radius: 4px;
}

.tab-switch-btn:not(:disabled):hover {
  color: #66717f;
  background: #e1e4e9;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__nav-wrap),
:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__nav-wrap::after),
:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__nav-wrap::before) {
  border: none !important;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__nav) {
  border: none;
  gap: 2px;
  background: transparent;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__item) {
  height: 38px;
  line-height: 38px;
  border: none !important;
  border-radius: 6px 6px 0 0;
  background: #e6e9ee;
  color: #737b88;
  padding: 0 16px;
  margin-top: 0;
  font-weight: 400;
  transition: all 0.18s ease;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__item:hover) {
  color: #5f6878;
  background: #dde2ea;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__item.is-active) {
  color: #3f68d2;
  background: #e9f0ff;
  border-radius: 6px 6px 0 0;
  box-shadow: inset 0 -1px 0 #e9f0ff;
  font-weight: 500;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__item .is-icon-close) {
  color: #8a94a6;
  border-radius: 50%;
}

:deep(.app-tabs.el-tabs--card > .el-tabs__header .el-tabs__item .is-icon-close:hover) {
  color: #344054;
  background: #e8ecf3;
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
