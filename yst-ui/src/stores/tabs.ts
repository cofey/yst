import type { RouteLocationNormalizedLoaded } from "vue-router";
import { defineStore } from "pinia";

const TABS_STORAGE_KEY = "tabs_state";
const MAX_TABS = 20;

export interface TabItem {
  key: string;
  title: string;
  fullPath: string;
  name: string;
  closable: boolean;
  cacheable: boolean;
  affix: boolean;
}

interface TabsState {
  visitedTabs: TabItem[];
  activeTabKey: string;
  cachedViewKeys: string[];
}

interface PersistedTabsState {
  visitedTabs: TabItem[];
  activeTabKey: string;
  cachedViewKeys: string[];
}

function stringifyValue(value: unknown): string {
  if (Array.isArray(value)) {
    return value.map((item) => stringifyValue(item)).join(",");
  }
  if (value === undefined || value === null) {
    return "";
  }
  return String(value);
}

function buildParamSignature(params: RouteLocationNormalizedLoaded["params"]): string {
  const entries = Object.entries(params || {})
    .map(([key, value]) => [key, stringifyValue(value)] as const)
    .filter(([, value]) => value !== "")
    .sort(([a], [b]) => a.localeCompare(b));

  if (!entries.length) {
    return "";
  }
  return entries.map(([key, value]) => `${key}=${value}`).join("&");
}

export function buildTabKey(route: Pick<RouteLocationNormalizedLoaded, "name" | "path" | "params">) {
  const base = route.name ? String(route.name) : route.path;
  const signature = buildParamSignature(route.params || {});
  return signature ? `${base}::${signature}` : base;
}

function isTabRoute(route: Pick<RouteLocationNormalizedLoaded, "path" | "name" | "meta">) {
  if (route.path === "/login") {
    return false;
  }
  if (route.meta?.tab === false) {
    return false;
  }
  return Boolean(route.name);
}

function resolveTabTitle(route: Pick<RouteLocationNormalizedLoaded, "meta" | "name" | "params" | "query">) {
  const fixedTitle = route.meta?.title ? String(route.meta.title) : "";
  if (route.name === "dict-data") {
    const dictName = route.query?.dictName ? String(route.query.dictName) : "";
    const dictType = route.params?.dictType ? String(route.params.dictType) : "";
    if (dictName) {
      return `${dictName}数据`;
    }
    if (dictType) {
      return `字典数据(${dictType})`;
    }
  }
  if (fixedTitle) {
    return fixedTitle;
  }
  return route.name ? String(route.name) : "未命名页面";
}

function toTabItem(route: RouteLocationNormalizedLoaded): TabItem {
  const affix = Boolean(route.meta?.affix);
  const cacheable = Boolean(route.meta?.cache);
  return {
    key: buildTabKey(route),
    title: resolveTabTitle(route),
    fullPath: route.fullPath,
    name: route.meta?.keepAliveName
      ? String(route.meta.keepAliveName)
      : route.name
        ? String(route.name)
        : route.path,
    closable: !affix,
    cacheable,
    affix
  };
}

function readState(): TabsState {
  const raw = localStorage.getItem(TABS_STORAGE_KEY);
  if (!raw) {
    return {
      visitedTabs: [],
      activeTabKey: "",
      cachedViewKeys: []
    };
  }

  try {
    const parsed = JSON.parse(raw) as PersistedTabsState;
    const visitedTabs = Array.isArray(parsed?.visitedTabs) ? parsed.visitedTabs : [];
    const activeTabKey = typeof parsed?.activeTabKey === "string" ? parsed.activeTabKey : "";
    const cachedViewKeys = Array.isArray(parsed?.cachedViewKeys)
      ? parsed.cachedViewKeys.filter((key) => typeof key === "string")
      : [];
    return {
      visitedTabs,
      activeTabKey,
      cachedViewKeys
    };
  } catch {
    return {
      visitedTabs: [],
      activeTabKey: "",
      cachedViewKeys: []
    };
  }
}

function persistState(state: TabsState) {
  const payload: PersistedTabsState = {
    visitedTabs: state.visitedTabs,
    activeTabKey: state.activeTabKey,
    cachedViewKeys: state.cachedViewKeys
  };
  localStorage.setItem(TABS_STORAGE_KEY, JSON.stringify(payload));
}

function clampTabs(tabs: TabItem[]) {
  if (tabs.length <= MAX_TABS) {
    return tabs;
  }
  const affixTabs = tabs.filter((tab) => tab.affix);
  const dynamicTabs = tabs.filter((tab) => !tab.affix);
  const keepDynamicCount = Math.max(0, MAX_TABS - affixTabs.length);
  const keptDynamicTabs = dynamicTabs.slice(-keepDynamicCount);
  return [...affixTabs, ...keptDynamicTabs];
}

export function getTabsStorageKey() {
  return TABS_STORAGE_KEY;
}

export const useTabsStore = defineStore("tabs", {
  state: (): TabsState => readState(),
  actions: {
    openTab(route: RouteLocationNormalizedLoaded) {
      if (!isTabRoute(route)) {
        return;
      }
      const current = toTabItem(route);
      const index = this.visitedTabs.findIndex((tab) => tab.key === current.key);
      if (index >= 0) {
        this.visitedTabs[index] = current;
      } else {
        this.visitedTabs.push(current);
        this.visitedTabs = clampTabs(this.visitedTabs);
      }

      if (!this.visitedTabs.some((tab) => tab.key === current.key)) {
        const fallback = this.visitedTabs[this.visitedTabs.length - 1];
        this.activeTabKey = fallback?.key || "";
      } else {
        this.activeTabKey = current.key;
      }

      this.syncCachedViews();
      persistState(this.$state);
    },
    setActiveTab(key: string) {
      if (!this.visitedTabs.some((tab) => tab.key === key)) {
        return;
      }
      this.activeTabKey = key;
      persistState(this.$state);
    },
    closeTab(key: string) {
      const index = this.visitedTabs.findIndex((tab) => tab.key === key);
      if (index < 0) {
        return this.getActivePath();
      }
      if (!this.visitedTabs[index].closable) {
        return this.getActivePath();
      }

      const isActive = this.activeTabKey === key;
      this.visitedTabs.splice(index, 1);
      if (!this.visitedTabs.length) {
        this.activeTabKey = "";
      } else if (isActive) {
        const next = this.visitedTabs[index] || this.visitedTabs[index - 1] || this.visitedTabs[0];
        this.activeTabKey = next.key;
      }

      this.syncCachedViews();
      persistState(this.$state);
      return this.getActivePath();
    },
    closeOtherTabs(key: string) {
      const kept = this.visitedTabs.filter((tab) => tab.affix || tab.key === key);
      this.visitedTabs = kept;
      const exists = kept.some((tab) => tab.key === this.activeTabKey);
      if (!exists) {
        this.activeTabKey = key;
      }
      this.syncCachedViews();
      persistState(this.$state);
      return this.getActivePath();
    },
    restore() {
      const restored = readState();
      this.visitedTabs = restored.visitedTabs;
      this.activeTabKey = restored.activeTabKey;
      this.cachedViewKeys = restored.cachedViewKeys;
    },
    reset() {
      this.visitedTabs = [];
      this.activeTabKey = "";
      this.cachedViewKeys = [];
      localStorage.removeItem(TABS_STORAGE_KEY);
    },
    getTabByKey(key: string) {
      return this.visitedTabs.find((tab) => tab.key === key);
    },
    getActivePath() {
      const active = this.getTabByKey(this.activeTabKey);
      return active?.fullPath || "";
    },
    syncCachedViews() {
      const cacheNames = this.visitedTabs
        .filter((tab) => tab.cacheable)
        .map((tab) => tab.name)
        .filter((name, index, arr) => arr.indexOf(name) === index);
      this.cachedViewKeys = cacheNames;
    }
  }
});
