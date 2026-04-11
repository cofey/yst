import { describe, expect, it, vi, beforeEach } from "vitest";
import { flushPromises, mount } from "@vue/test-utils";
import { createPinia, setActivePinia } from "pinia";
import ElementPlus from "element-plus";
import DictTypeView from "@/modules/system/dict/pages/index.vue";
import { useAuthStore } from "@/stores/auth";

const mockListDictTypesApi = vi.fn();
const mockRouterPush = vi.fn();

vi.mock("@/modules/system/dict/api", () => ({
  listDictTypesApi: (...args: unknown[]) => mockListDictTypesApi(...args),
  createDictTypeApi: vi.fn(),
  updateDictTypeApi: vi.fn(),
  deleteDictTypeApi: vi.fn(),
  createDictDataApi: vi.fn(),
  updateDictDataApi: vi.fn(),
  deleteDictDataApi: vi.fn(),
  listDictDataApi: vi.fn()
}));

vi.mock("@/composables/useDict", () => ({
  clearDictCache: vi.fn(),
  loadDictOptions: vi.fn().mockResolvedValue([]),
  getDictLabel: vi.fn().mockImplementation((_options, value) => String(value)),
  getDictTagType: vi.fn().mockReturnValue("info")
}));

vi.mock("vue-router", async () => {
  const actual = await vi.importActual("vue-router");
  return {
    ...actual,
    useRouter: () => ({
      push: mockRouterPush
    })
  };
});

describe("DictTypeView", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  it("应在点击数据按钮后跳转到字典数据维护页", async () => {
    mockListDictTypesApi.mockResolvedValue({
      records: [{ dictId: "1", dictName: "状态", dictType: "sys_common_status", status: 1 }],
      total: 1
    });

    const pinia = createPinia();
    setActivePinia(pinia);
    const authStore = useAuthStore();
    authStore.setAuth({
      token: "t",
      userId: "u-1",
      username: "admin",
      roles: ["admin"],
      permissions: []
    });

    const wrapper = mount(DictTypeView, {
      global: {
        plugins: [pinia, ElementPlus],
        directives: {
          hasPermi: {
            mounted() {
              return;
            }
          }
        }
      }
    });

    await flushPromises();
    const queryButton = wrapper.findAll("button").find((btn) => btn.text() === "查询");
    expect(queryButton).toBeTruthy();
    await queryButton!.trigger("click");
    await flushPromises();

    const buttons = wrapper.findAll("button");
    const dataButton = buttons.find((btn) => btn.text() === "数据");
    expect(dataButton).toBeTruthy();
    await dataButton!.trigger("click");

    expect(mockRouterPush).toHaveBeenCalledWith({
      name: "dict-data",
      params: { dictType: "sys_common_status" },
      query: { dictName: "状态" }
    });
  });
});
