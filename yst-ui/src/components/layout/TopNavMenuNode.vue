<template>
  <el-sub-menu
    v-if="hasChildren"
    :index="menuIndex"
    popper-class="top-menu-popper"
  >
    <template #title>
      <el-icon><component :is="menuIcon" /></el-icon>
      <span>{{ node.menuName }}</span>
    </template>
    <TopNavMenuNode
      v-for="child in node.children"
      :key="child.menuId"
      :node="child"
    />
  </el-sub-menu>
  <el-menu-item v-else :index="menuIndex">
    <el-icon><component :is="menuIcon" /></el-icon>
    <span>{{ node.menuName }}</span>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from "vue";
import * as EpIcons from "@element-plus/icons-vue";
import { Menu } from "@element-plus/icons-vue";
import type { AuthMenu } from "@/modules/auth/types";

defineOptions({
  name: "TopNavMenuNode"
});

const props = defineProps<{
  node: AuthMenu;
}>();

const hasChildren = computed(() => Boolean(props.node.children?.length));
const menuIndex = computed(() =>
  props.node.path ? `route:${props.node.path}` : `menu:${props.node.menuId}`
);
const menuIcon = computed(() => {
  const iconName = props.node.icon;
  if (!iconName) {
    return Menu;
  }
  return (EpIcons as Record<string, any>)[iconName] || Menu;
});
</script>
