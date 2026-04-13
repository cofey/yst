<template>
  <span class="dict-tag-wrap">
    <template v-if="selectedOptions.length">
      <el-tag
        v-for="item in selectedOptions"
        :key="item.value"
        :type="resolveDictTagType(item.listClass)"
        class="dict-tag-item"
        disable-transitions
      >
        {{ item.label }}
      </el-tag>
    </template>
    <span v-else>{{ fallbackText }}</span>
  </span>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { resolveDictTagType } from "@/modules/system/dict/dict";
import type { DictOptionItem } from "@/modules/system/dict/types";

const props = withDefaults(
  defineProps<{
    options: DictOptionItem[];
    value: string | number | Array<string | number> | undefined | null;
    separator?: string;
    fallback?: string;
  }>(),
  {
    separator: ",",
    fallback: "-"
  }
);

const parsedValues = computed(() => {
  if (Array.isArray(props.value)) {
    return props.value.map((item) => String(item));
  }
  if (props.value === undefined || props.value === null || props.value === "") {
    return [];
  }
  return String(props.value)
    .split(props.separator)
    .map((item) => item.trim())
    .filter(Boolean);
});

const selectedOptions = computed(() => {
  if (!parsedValues.value.length) {
    return [];
  }
  const valueSet = new Set(parsedValues.value);
  return props.options.filter((item) => valueSet.has(String(item.value)));
});

const fallbackText = computed(() => {
  if (props.value === undefined || props.value === null || props.value === "") {
    return props.fallback;
  }
  return Array.isArray(props.value) ? props.value.join(props.separator) : String(props.value);
});
</script>

<style scoped>
.dict-tag-wrap {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.dict-tag-item {
  margin: 0;
}
</style>
