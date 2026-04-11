import { listDictOptionsApi } from "@/modules/system/dict/api";
import type { DictOptionItem } from "@/modules/system/dict/types";

const dictCache = new Map<string, DictOptionItem[]>();

export async function loadDictOptions(dictType: string, forceRefresh = false) {
  if (!forceRefresh && dictCache.has(dictType)) {
    return dictCache.get(dictType) || [];
  }
  const options = await listDictOptionsApi(dictType);
  dictCache.set(dictType, options);
  return options;
}

export function clearDictCache(dictType?: string) {
  if (dictType) {
    dictCache.delete(dictType);
    return;
  }
  dictCache.clear();
}

export function getDictLabel(options: DictOptionItem[], value: string | number, fallback = "-") {
  const target = String(value);
  const item = options.find((option) => option.value === target);
  return item?.label || fallback;
}

const dictTagTypeMap: Record<string, "primary" | "success" | "warning" | "danger" | "info"> = {
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

export function getDictTagType(
  options: DictOptionItem[],
  value: string | number,
  fallback = "info"
) {
  const target = String(value);
  const item = options.find((option) => option.value === target);
  const listClass = item?.listClass?.trim().toLowerCase();
  if (!listClass) {
    return fallback;
  }
  return dictTagTypeMap[listClass] || fallback;
}
