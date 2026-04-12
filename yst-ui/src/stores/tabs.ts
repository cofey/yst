import { defineStore } from "pinia";
import type { RouteLocationNormalizedLoaded } from "vue-router";

const TABS_STORAGE_KEY = "yst_tabs_state";

export interface AppTabItem {
  key: string;
  title: string;
  fullPath: string;
  path: string;
  closable: boolean;
  cache: boolean;
  keepAliveName: string;
}

interface TabsState {
  activeTabKey: string;
  visitedTabs: AppTabItem[];
}

function readTabsState(): TabsState {
  const raw = localStorage.getItem(TABS_STORAGE_KEY);
  if (!raw) {
    return {
      activeTabKey: "",
      visitedTabs: []
    };
  }
  try {
    const parsed = JSON.parse(raw);
    const visitedTabs = Array.isArray(parsed?.visitedTabs)
      ? parsed.visitedTabs
          .map((item: Partial<AppTabItem>) => ({
            key: String(item?.key || ""),
            title: String(item?.title || ""),
            fullPath: String(item?.fullPath || ""),
            path: String(item?.path || ""),
            closable: item?.closable !== false,
            cache: item?.cache === true,
            keepAliveName: String(item?.keepAliveName || "")
          }))
          .filter((item: AppTabItem) => item.key && item.fullPath)
      : [];
    const activeTabKey = String(parsed?.activeTabKey || "");
    return {
      activeTabKey,
      visitedTabs
    };
  } catch {
    return {
      activeTabKey: "",
      visitedTabs: []
    };
  }
}

function persistTabsState(state: TabsState) {
  localStorage.setItem(
    TABS_STORAGE_KEY,
    JSON.stringify({
      activeTabKey: state.activeTabKey,
      visitedTabs: state.visitedTabs
    })
  );
}

function resolveTitle(route: RouteLocationNormalizedLoaded): string {
  if (typeof route.meta.title === "string" && route.meta.title.trim()) {
    return route.meta.title.trim();
  }
  if (typeof route.name === "string" && route.name.trim()) {
    return route.name.trim();
  }
  return route.path || "未命名页面";
}

function resolveKeepAliveName(route: RouteLocationNormalizedLoaded): string {
  if (typeof route.meta.keepAliveName === "string" && route.meta.keepAliveName.trim()) {
    return route.meta.keepAliveName.trim();
  }
  if (typeof route.name === "string" && route.name.trim()) {
    return route.name.trim();
  }
  return "";
}

function buildTabFromRoute(route: RouteLocationNormalizedLoaded): AppTabItem {
  return {
    key: buildTabKey(route),
    title: resolveTitle(route),
    fullPath: route.fullPath,
    path: route.path,
    closable: route.meta.affix !== true,
    cache: route.meta.cache === true,
    keepAliveName: resolveKeepAliveName(route)
  };
}

export function buildTabKey(route: RouteLocationNormalizedLoaded): string {
  return route.fullPath;
}

export const useTabsStore = defineStore("tabs", {
  state: (): TabsState => ({
    activeTabKey: "",
    visitedTabs: []
  }),
  getters: {
    cachedViewKeys(state: TabsState): string[] {
      return state.visitedTabs
        .filter((item: AppTabItem) => item.cache && item.keepAliveName)
        .map((item: AppTabItem) => item.keepAliveName);
    }
  },
  actions: {
    openTab(route: RouteLocationNormalizedLoaded) {
      const nextTab = buildTabFromRoute(route);
      const existingIndex = this.visitedTabs.findIndex((item) => item.key === nextTab.key);
      if (existingIndex >= 0) {
        this.visitedTabs[existingIndex] = {
          ...this.visitedTabs[existingIndex],
          ...nextTab
        };
      } else {
        this.visitedTabs.push(nextTab);
      }
      this.activeTabKey = nextTab.key;
      persistTabsState(this.$state);
    },
    setActiveTab(key: string) {
      if (!key) {
        return;
      }
      if (!this.visitedTabs.some((item) => item.key === key)) {
        return;
      }
      this.activeTabKey = key;
      persistTabsState(this.$state);
    },
    closeTab(key: string): string {
      const index = this.visitedTabs.findIndex((item) => item.key === key);
      if (index < 0) {
        return this.getActivePath();
      }

      const target = this.visitedTabs[index];
      if (!target.closable) {
        return this.getActivePath();
      }

      const isActive = this.activeTabKey === key;
      this.visitedTabs.splice(index, 1);

      if (!this.visitedTabs.length) {
        this.activeTabKey = "";
        persistTabsState(this.$state);
        return "";
      }

      if (!isActive) {
        persistTabsState(this.$state);
        return "";
      }

      const nextIndex = index < this.visitedTabs.length ? index : this.visitedTabs.length - 1;
      const nextTab = this.visitedTabs[nextIndex];
      this.activeTabKey = nextTab.key;
      persistTabsState(this.$state);
      return nextTab.fullPath;
    },
    getTabByKey(key: string): AppTabItem | undefined {
      return this.visitedTabs.find((item) => item.key === key);
    },
    getActivePath(): string {
      if (!this.visitedTabs.length) {
        return "";
      }
      const active = this.getTabByKey(this.activeTabKey);
      return active?.fullPath || this.visitedTabs[0].fullPath;
    },
    restore() {
      const restored = readTabsState();
      this.visitedTabs = restored.visitedTabs;
      const hasActive =
        restored.activeTabKey &&
        restored.visitedTabs.some((item) => item.key === restored.activeTabKey);
      this.activeTabKey = hasActive ? restored.activeTabKey : restored.visitedTabs[0]?.key || "";
      persistTabsState(this.$state);
    },
    reset() {
      this.activeTabKey = "";
      this.visitedTabs = [];
      localStorage.removeItem(TABS_STORAGE_KEY);
    }
  }
});
