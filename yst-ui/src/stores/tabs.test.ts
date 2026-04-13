import { beforeEach, describe, expect, it } from "vitest";
import { createPinia, setActivePinia } from "pinia";
import type { RouteLocationNormalizedLoaded } from "vue-router";
import { buildTabKey, getTabsStorageKey, useTabsStore } from "@/stores/tabs";

function createRoute(input: {
  name: string;
  path: string;
  fullPath?: string;
  params?: Record<string, string>;
  query?: Record<string, string>;
  meta?: Record<string, unknown>;
}) {
  return {
    name: input.name,
    path: input.path,
    fullPath: input.fullPath || input.path,
    params: input.params || {},
    query: input.query || {},
    meta: input.meta || {},
    matched: [{}]
  } as unknown as RouteLocationNormalizedLoaded;
}

describe("tabs store", () => {
  beforeEach(() => {
    localStorage.clear();
    setActivePinia(createPinia());
  });

  it("应支持打开、关闭与关闭其他标签", () => {
    const store = useTabsStore();
    const home = createRoute({
      name: "home",
      path: "/home",
      meta: { title: "首页", affix: true, cache: true, tab: true }
    });
    const users = createRoute({
      name: "users",
      path: "/system/users",
      meta: { title: "用户管理", cache: true, tab: true }
    });
    const roles = createRoute({
      name: "roles",
      path: "/system/roles",
      meta: { title: "角色管理", cache: true, tab: true }
    });

    store.openTab(home);
    store.openTab(users);
    store.openTab(roles);
    expect(store.visitedTabs).toHaveLength(3);
    expect(store.activeTabKey).toBe("roles");

    const nextPath = store.closeTab("roles");
    expect(nextPath).toBe("/system/users");
    expect(store.activeTabKey).toBe("users");

    store.closeOtherTabs("users");
    expect(store.visitedTabs.map((tab) => tab.key)).toEqual(["home", "users"]);
  });

  it("应按动态参数生成不同 tabKey", () => {
    const a = createRoute({
      name: "dict-data",
      path: "/system/dicts/:dictType/data",
      fullPath: "/system/dicts/gender/data",
      params: { dictType: "gender" },
      meta: { title: "字典数据", cache: true, tab: true }
    });
    const b = createRoute({
      name: "dict-data",
      path: "/system/dicts/:dictType/data",
      fullPath: "/system/dicts/status/data",
      params: { dictType: "status" },
      meta: { title: "字典数据", cache: true, tab: true }
    });

    expect(buildTabKey(a)).not.toBe(buildTabKey(b));
  });

  it("应持久化并恢复标签状态", () => {
    const store = useTabsStore();
    store.openTab(
      createRoute({
        name: "home",
        path: "/home",
        meta: { title: "首页", affix: true, cache: true, tab: true }
      })
    );
    store.openTab(
      createRoute({
        name: "users",
        path: "/system/users",
        meta: { title: "用户管理", cache: true, tab: true }
      })
    );

    const storageRaw = localStorage.getItem(getTabsStorageKey());
    expect(storageRaw).toBeTruthy();

    setActivePinia(createPinia());
    const restored = useTabsStore();
    restored.restore();
    expect(restored.visitedTabs.map((tab) => tab.key)).toEqual(["home", "users"]);
    expect(restored.activeTabKey).toBe("users");
  });

  it("应在超过上限时保留固定标签和最新非固定标签", () => {
    const store = useTabsStore();
    store.openTab(
      createRoute({
        name: "home",
        path: "/home",
        meta: { title: "首页", affix: true, cache: true, tab: true }
      })
    );

    for (let i = 1; i <= 30; i += 1) {
      store.openTab(
        createRoute({
          name: "dict-data",
          path: "/system/dicts/:dictType/data",
          fullPath: `/system/dicts/type-${i}/data`,
          params: { dictType: `type-${i}` },
          meta: { title: "字典数据", cache: true, tab: true }
        })
      );
    }

    expect(store.visitedTabs).toHaveLength(20);
    expect(store.visitedTabs.some((tab) => tab.key === "home")).toBe(true);
    expect(
      store.visitedTabs.some((tab) => tab.key === "dict-data::dictType=type-30")
    ).toBe(true);
    expect(
      store.visitedTabs.some((tab) => tab.key === "dict-data::dictType=type-1")
    ).toBe(false);
  });
});

