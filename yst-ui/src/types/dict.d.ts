import type { Ref } from "vue";
import type { DictOptionItem } from "@/modules/system/dict/types";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    useDict<T extends string>(...dictTypes: T[]): Record<T, Ref<DictOptionItem[]>>;
  }
}

export {};
