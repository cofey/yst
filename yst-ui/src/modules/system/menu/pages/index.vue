<template>
  <div class="page">
    <el-card>
      <div class="table-tools">
        <el-button v-hasPermi="['system:menu:add']" class="toolbar-btn" type="success" @click="openCreate"
          >新增</el-button
        >
        <el-button
          v-hasPermi="['system:menu:edit']"
          class="toolbar-btn"
          type="warning"
          plain
          @click="clearCache"
          >清缓存</el-button
        >
      </div>
      <el-table :data="treeData" border row-key="menuId" :tree-props="{ children: 'children' }">
        <el-table-column label="名称" min-width="220" class-name="tree-list-name-col">
          <template #default="{ row }">
            <div class="tree-list-name-cell">
              <el-icon v-if="resolveIconComponent(row.icon)" class="menu-icon">
                <component :is="resolveIconComponent(row.icon)" />
              </el-icon>
              <span>{{ row.menuName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="menuTypeTagType(row.menuType)">{{ menuTypeLabel(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="图标" width="120">
          <template #default="{ row }">
            <div class="menu-icon-cell">
              <el-icon v-if="resolveIconComponent(row.icon)">
                <component :is="resolveIconComponent(row.icon)" />
              </el-icon>
              <span>{{ row.icon || "-" }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由" min-width="150" />
        <el-table-column prop="component" label="组件" min-width="180" />
        <el-table-column prop="perms" label="权限标识" min-width="170" />
        <el-table-column prop="sort" label="排序" width="90" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-hasPermi="['system:menu:edit']" link type="primary" @click="openEdit(row)"
              >编辑</el-button
            >
            <el-button
              v-hasPermi="['system:menu:remove']"
              link
              type="danger"
              @click="remove(row.menuId)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑菜单' : '新增菜单'" width="560px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'menuName', children: 'children', value: 'menuId' }"
            check-strictly
            default-expand-all
            clearable
            placeholder="根节点"
          />
        </el-form-item>
        <el-form-item label="菜单名称" required>
          <el-input v-model="form.menuName" />
        </el-form-item>
        <el-form-item label="菜单类型" required>
          <el-select v-model="form.menuType" style="width: 100%">
            <el-option
              v-for="item in menuTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="路由地址">
          <el-input v-model="form.path" />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="form.component" />
        </el-form-item>
        <el-form-item label="菜单图标">
          <div class="icon-form">
            <el-input v-model="form.icon" readonly placeholder="请选择图标或清空" />
            <el-button type="primary" plain @click="iconPickerVisible = true">选择</el-button>
            <el-button @click="form.icon = ''">清空</el-button>
          </div>
          <div v-if="resolveIconComponent(form.icon)" class="icon-preview">
            <el-icon><component :is="resolveIconComponent(form.icon)" /></el-icon>
            <span>{{ form.icon }}</span>
          </div>
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="显示">
          <el-radio-group v-model="form.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="iconPickerVisible" title="选择图标" width="760px">
      <el-input
        v-model="iconKeyword"
        class="icon-search"
        clearable
        placeholder="输入图标名称过滤，例如 User、Setting"
      />
      <div class="icon-grid">
        <button
          v-for="icon in filteredIcons"
          :key="icon"
          type="button"
          class="icon-grid-item"
          @click="selectIcon(icon)"
        >
          <el-icon><component :is="resolveIconComponent(icon)" /></el-icon>
          <span class="icon-name">{{ icon }}</span>
        </button>
      </div>
      <template #footer>
        <el-button @click="iconPickerVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import type { Component } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as EpIcons from "@element-plus/icons-vue";
import {
  clearAllMenuAuthCacheApi,
  createMenuApi,
  deleteMenuApi,
  listMenusApi,
  updateMenuApi
} from "@/modules/system/menu/api";
import { useAuthStore } from "@/stores/auth";
import type { MenuItem, MenuTreeItem } from "@/modules/system/menu/types";
import type { DictOptionItem } from "@/modules/system/dict/types";
import { getDictLabel, getDictTagType, loadDictOptions } from "@/composables/useDict";

defineOptions({
  name: "SystemMenuPage"
});

const authStore = useAuthStore();
const flatData = ref<MenuItem[]>([]);
const statusOptions = ref<DictOptionItem[]>([]);
const menuTypeOptions = ref<DictOptionItem[]>([]);
const dialogVisible = ref(false);
const editingId = ref<string | null>(null);
const form = reactive({
  parentId: null as string | null,
  menuName: "",
  menuType: "" as "" | "M" | "C" | "F",
  path: "",
  component: "",
  icon: "",
  perms: "",
  sort: 0,
  status: 1,
  visible: 1
});
const iconPickerVisible = ref(false);
const iconKeyword = ref("");

const allIconNames = Object.keys(EpIcons).sort((a, b) => a.localeCompare(b));

const filteredIcons = computed(() => {
  const keyword = iconKeyword.value.trim().toLowerCase();
  if (!keyword) {
    return allIconNames;
  }
  return allIconNames.filter((name) => name.toLowerCase().includes(keyword));
});

const buildTree = (items: MenuItem[]): MenuTreeItem[] => {
  const map = new Map<string, MenuTreeItem>();
  const roots: MenuTreeItem[] = [];
  const sorted = [...items].sort((a, b) => a.sort - b.sort || a.menuId.localeCompare(b.menuId));
  for (const item of sorted) {
    map.set(item.menuId, { ...item, children: [] });
  }
  for (const item of sorted) {
    const node = map.get(item.menuId)!;
    if (!item.parentId) {
      roots.push(node);
      continue;
    }
    const parent = map.get(item.parentId);
    if (parent) {
      parent.children = parent.children || [];
      parent.children.push(node);
    } else {
      roots.push(node);
    }
  }
  return roots;
};

const treeData = computed(() => buildTree(flatData.value));
const parentOptions = computed(() => treeData.value);

const loadData = async () => {
  flatData.value = await listMenusApi();
};

const resetForm = () => {
  form.parentId = null;
  form.menuName = "";
  form.menuType = (menuTypeOptions.value[0]?.value as "" | "M" | "C" | "F") || "";
  form.path = "";
  form.component = "";
  form.icon = "";
  form.perms = "";
  form.sort = 0;
  form.status = 1;
  form.visible = 1;
};

const openCreate = () => {
  editingId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: MenuItem) => {
  editingId.value = row.menuId;
  form.parentId = row.parentId || null;
  form.menuName = row.menuName;
  form.menuType = row.menuType;
  form.path = row.path || "";
  form.component = row.component || "";
  form.icon = row.icon || "";
  form.perms = row.perms || "";
  form.sort = row.sort;
  form.status = row.status;
  form.visible = row.visible;
  dialogVisible.value = true;
};

const submit = async () => {
  if (!form.menuName) {
    ElMessage.warning("请填写菜单名称");
    return;
  }
  if (!form.menuType) {
    ElMessage.warning("请选择菜单类型");
    return;
  }
  const payload = { ...form, parentId: form.parentId || null };
  if (editingId.value) {
    await updateMenuApi(editingId.value, payload);
  } else {
    await createMenuApi(payload);
  }
  ElMessage.success("操作成功");
  dialogVisible.value = false;
  await loadData();
};

const remove = async (menuId: string) => {
  await ElMessageBox.confirm("确认删除该菜单吗？", "提示", { type: "warning" });
  await deleteMenuApi(menuId);
  ElMessage.success("删除成功");
  await loadData();
};

const clearCache = async () => {
  await ElMessageBox.confirm("确认清理菜单权限缓存吗？", "提示", { type: "warning" });
  await clearAllMenuAuthCacheApi();
  ElMessage.success("缓存清理成功");
};

const resolveIconComponent = (iconName?: string) => {
  if (!iconName) {
    return null;
  }
  return (EpIcons as Record<string, Component>)[iconName] || null;
};

const selectIcon = (iconName: string) => {
  form.icon = iconName;
  iconPickerVisible.value = false;
};

const menuTypeLabel = (value: MenuItem["menuType"]) =>
  getDictLabel(menuTypeOptions.value, value, value);

const menuTypeTagType = (value: MenuItem["menuType"]) =>
  getDictTagType(menuTypeOptions.value, value, "info");

onMounted(async () => {
  if (!authStore.hasPermi("system:menu:list")) {
    return;
  }
  const [statuses, menuTypes] = await Promise.all([
    loadDictOptions("sys_common_status"),
    loadDictOptions("sys_menu_type")
  ]);
  statusOptions.value = statuses;
  menuTypeOptions.value = menuTypes;
  if (menuTypeOptions.value.length === 0) {
    ElMessage.warning("字典 sys_menu_type 未配置，菜单类型不可用");
  }
  await loadData();
});
</script>

<style scoped>
.menu-icon {
  color: #606266;
}

.menu-icon-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.icon-form {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-preview {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
}

.icon-search {
  margin-bottom: 12px;
}

.icon-grid {
  max-height: 420px;
  overflow: auto;
  padding: 4px;
  display: grid;
  grid-template-columns: repeat(6, minmax(100px, 1fr));
  gap: 8px;
}

.icon-grid-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  color: #303133;
  padding: 10px 8px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.icon-grid-item:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.icon-name {
  width: 100%;
  font-size: 12px;
  color: #606266;
  text-align: center;
  word-break: break-all;
}

@media (max-width: 1200px) {
  .icon-grid {
    grid-template-columns: repeat(4, minmax(100px, 1fr));
  }
}
</style>
