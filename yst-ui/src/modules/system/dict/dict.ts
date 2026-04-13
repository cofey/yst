import type { App, Ref } from "vue";
import { ref } from "vue";
import { listDictOptionsApi } from "@/modules/system/dict/api";
import type { DictOptionItem } from "@/modules/system/dict/types";

type DictTagType = "primary" | "success" | "warning" | "danger" | "info";

const dictCache = new Map<string, DictOptionItem[]>();
const dictPending = new Map<string, Promise<DictOptionItem[]>>();
const dictConsumers = new Map<string, Set<Ref<DictOptionItem[]>>>();

const dictTagTypeMap: Record<string, DictTagType> = {
  primary: "primary",
  success: "success",
  warning: "warning",
  warn: "warning",
  danger: "danger",
  error: "danger",
  info: "info",
  default: "info",
  secondary: "info"
};

function normalizeDictOptions(options: DictOptionItem[]) {
  return options.map((item) => ({
    ...item,
    value: String(item.value)
  }));
}

async function fetchDictOptions(dictType: string) {
  const cached = dictCache.get(dictType);
  if (cached) {
    return cached;
  }
  const pending = dictPending.get(dictType);
  if (pending) {
    return pending;
  }
  const request = listDictOptionsApi(dictType)
    .then((options) => {
      const normalized = normalizeDictOptions(options || []);
      dictCache.set(dictType, normalized);
      return normalized;
    })
    .finally(() => {
      dictPending.delete(dictType);
    });
  dictPending.set(dictType, request);
  return request;
}

export function useDict<T extends string>(...dictTypes: T[]): Record<T, Ref<DictOptionItem[]>> {
  const dictState = {} as Record<T, Ref<DictOptionItem[]>>;
  for (const dictType of dictTypes) {
    dictState[dictType] = ref([]);
    const consumers = dictConsumers.get(dictType) || new Set<Ref<DictOptionItem[]>>();
    consumers.add(dictState[dictType]);
    dictConsumers.set(dictType, consumers);
    fetchDictOptions(dictType)
      .then((options) => {
        dictState[dictType].value = options;
      })
      .catch(() => {
        dictState[dictType].value = [];
      });
  }
  return dictState;
}

export function setupDict(app: App) {
  app.config.globalProperties.useDict = useDict;
}

export function clearDictCache(dictType?: string) {
  const refreshDict = (currentType: string) => {
    const consumers = dictConsumers.get(currentType);
    if (!consumers || consumers.size === 0) {
      return;
    }
    for (const consumer of consumers) {
      consumer.value = [];
    }
    fetchDictOptions(currentType)
      .then((options) => {
        for (const consumer of consumers) {
          consumer.value = options;
        }
      })
      .catch(() => {
        for (const consumer of consumers) {
          consumer.value = [];
        }
      });
  };

  if (dictType) {
    dictCache.delete(dictType);
    dictPending.delete(dictType);
    refreshDict(dictType);
    return;
  }
  const consumedTypes = Array.from(dictConsumers.keys());
  dictCache.clear();
  dictPending.clear();
  for (const currentType of consumedTypes) {
    refreshDict(currentType);
  }
}

export function resolveDictTagType(
  listClass?: string,
  fallback: DictTagType = "info"
): DictTagType {
  if (!listClass) {
    return fallback;
  }
  return dictTagTypeMap[listClass.trim().toLowerCase()] || fallback;
}
