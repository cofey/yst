import { describe, expect, it } from "vitest";
import { mount } from "@vue/test-utils";
import { createPinia, setActivePinia } from "pinia";
import { defineComponent, nextTick } from "vue";
import { useAuthStore } from "@/stores/auth";
import { setupHasPermiDirective } from "@/directives/hasPermi";

const DemoComponent = defineComponent({
  template: `
    <div>
      <button id="allowed" v-hasPermi="['system:dict:list']">查询</button>
      <button id="denied" v-hasPermi="['system:dict:remove']">删除</button>
    </div>
  `
});

describe("v-hasPermi", () => {
  it("应根据权限显隐按钮", async () => {
    localStorage.clear();
    const pinia = createPinia();
    setActivePinia(pinia);
    const authStore = useAuthStore();
    authStore.setAuth({
      token: "t",
      userId: "u-1",
      username: "u",
      roles: [],
      permissions: ["system:dict:list"]
    });

    const wrapper = mount(DemoComponent, {
      global: {
        plugins: [
          pinia,
          {
            install(app) {
              setupHasPermiDirective(app);
            }
          }
        ]
      }
    });

    await nextTick();
    expect(wrapper.find("#allowed").exists()).toBe(true);
    expect(wrapper.find("#denied").exists()).toBe(false);
  });
});
