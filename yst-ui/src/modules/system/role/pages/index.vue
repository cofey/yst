<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="toolbar-main">
          <div class="query-grid">
            <el-input
              v-model="query.roleName"
              class="query-field"
              placeholder="按角色名称搜索"
              clearable
              @keyup.enter="handleQuery"
            />
            <el-input
              v-model="query.roleKey"
              class="query-field"
              placeholder="按角色标识搜索"
              clearable
              @keyup.enter="handleQuery"
            />
            <el-select
              v-model="query.status"
              class="query-field"
              clearable
              placeholder="按状态筛选"
            >
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="Number(item.value)"
              />
            </el-select>
          </div>
          <div class="toolbar-actions right-actions">
            <el-button v-hasPermi="['system:role:list']" class="toolbar-btn" type="primary" @click="handleQuery"
              >查询</el-button
            >
            <el-button class="toolbar-btn" @click="handleReset">重置</el-button>
          </div>
        </div>
      </div>

      <div class="table-tools">
        <el-button v-hasPermi="['system:role:add']" class="toolbar-btn" type="success" @click="openCreate"
          >新增</el-button
        >
      </div>

      <el-table :data="tableData" border empty-text="暂无数据">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleKey" label="角色标识" min-width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime, "minute") }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-hasPermi="['system:role:edit']" link type="primary" @click="openEdit(row)"
              >编辑</el-button
            >
            <el-button
              v-hasPermi="['system:role:edit']"
              link
              type="warning"
              @click="openAssign(row)"
              >分配菜单</el-button
            >
            <el-button
              v-hasPermi="['system:role:remove']"
              link
              type="danger"
              @click="remove(row.roleId)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="pager-wrap">
        <el-pagination
          v-model:current-page="page.pageNum"
          v-model:page-size="page.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.total"
          @size-change="handlePageSizeChange"
          @current-change="handlePageNumChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑角色' : '新增角色'" width="520px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="角色名称" required>
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色标识" required>
          <el-input v-model="form.roleKey" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignVisible" title="分配菜单" width="680px">
      <el-tree
        ref="menuTreeRef"
        node-key="menuId"
        :data="menuTree"
        :props="treeProps"
        show-checkbox
        :check-strictly="false"
        default-expand-all
      />
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import type { ElTree } from "element-plus";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  assignRoleMenusApi,
  createRoleApi,
  deleteRoleApi,
  listRoleMenuIdsApi,
  listRolesApi,
  updateRoleApi
} from "@/modules/system/role/api";
import { listMenusApi } from "@/modules/system/menu/api";
import { useAuthStore } from "@/stores/auth";
import type { MenuItem, MenuTreeItem } from "@/modules/system/menu/types";
import type { RoleItem } from "@/modules/system/role/types";
import type { DictOptionItem } from "@/modules/system/dict/types";
import { getDictLabel, getDictTagType, loadDictOptions } from "@/composables/useDict";
import { formatDateTime } from "@/shared/utils/datetime";

defineOptions({
  name: "SystemRolePage"
});

const authStore = useAuthStore();

const query = reactive({
  roleName: "",
  roleKey: "",
  status: undefined as number | undefined
});
const queried = ref(false);

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const tableData = ref<RoleItem[]>([]);
const menuFlat = ref<MenuItem[]>([]);
const statusOptions = ref<DictOptionItem[]>([]);

const dialogVisible = ref(false);
const assignVisible = ref(false);
const editingId = ref<string | null>(null);
const assigningRoleId = ref<string | null>(null);
const menuTreeRef = ref<InstanceType<typeof ElTree>>();

const form = reactive({
  roleName: "",
  roleKey: "",
  status: 1
});

const treeProps = {
  label: "menuName",
  children: "children"
};

const menuTree = computed<MenuTreeItem[]>(() => {
  const map = new Map<string, MenuTreeItem>();
  const roots: MenuTreeItem[] = [];
  for (const item of menuFlat.value) {
    map.set(item.menuId, { ...item, children: [] });
  }
  for (const item of menuFlat.value) {
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
});

const loadRolePage = async () => {
  const res = await listRolesApi({
    roleName: query.roleName || undefined,
    roleKey: query.roleKey || undefined,
    status: query.status,
    pageNum: page.pageNum,
    pageSize: page.pageSize
  });
  tableData.value = res.records;
  page.total = res.total;
};

const handleQuery = async () => {
  page.pageNum = 1;
  queried.value = true;
  await loadRolePage();
};

const handleReset = async () => {
  query.roleName = "";
  query.roleKey = "";
  query.status = undefined;
  page.pageSize = 10;
  await handleQuery();
};

const handlePageNumChange = async (val: number) => {
  page.pageNum = val;
  if (!queried.value) {
    return;
  }
  await loadRolePage();
};

const handlePageSizeChange = async (val: number) => {
  page.pageSize = val;
  page.pageNum = 1;
  if (!queried.value) {
    return;
  }
  await loadRolePage();
};

const resetForm = () => {
  form.roleName = "";
  form.roleKey = "";
  form.status = 1;
};

const openCreate = () => {
  editingId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: RoleItem) => {
  editingId.value = row.roleId;
  form.roleName = row.roleName;
  form.roleKey = row.roleKey;
  form.status = row.status;
  dialogVisible.value = true;
};

const submit = async () => {
  if (!form.roleName || !form.roleKey) {
    ElMessage.warning("请填写角色名称和标识");
    return;
  }
  if (editingId.value) {
    await updateRoleApi(editingId.value, form);
  } else {
    await createRoleApi(form);
  }
  ElMessage.success("操作成功");
  dialogVisible.value = false;
  if (queried.value) {
    await loadRolePage();
  }
};

const remove = async (roleId: string) => {
  await ElMessageBox.confirm("确认删除该角色吗？", "提示", { type: "warning" });
  await deleteRoleApi(roleId);
  ElMessage.success("删除成功");
  if (queried.value) {
    await loadRolePage();
  }
};

const openAssign = async (row: RoleItem) => {
  assigningRoleId.value = row.roleId;
  assignVisible.value = true;
  await nextTick();
  const selectedIds = await listRoleMenuIdsApi(row.roleId);
  menuTreeRef.value?.setCheckedKeys(selectedIds, false);
};

const submitAssign = async () => {
  if (!assigningRoleId.value) {
    return;
  }
  const checked = (menuTreeRef.value?.getCheckedKeys(false) || []) as string[];
  const halfChecked = (menuTreeRef.value?.getHalfCheckedKeys() || []) as string[];
  const all = Array.from(new Set([...checked, ...halfChecked]));
  await assignRoleMenusApi(assigningRoleId.value, all);
  ElMessage.success("菜单分配成功");
  assignVisible.value = false;
};

const statusLabel = (value: number) => getDictLabel(statusOptions.value, String(value), "-");

const statusTagType = (value: number) => getDictTagType(statusOptions.value, String(value));

onMounted(async () => {
  if (!authStore.hasPermi("system:role:list")) {
    return;
  }
  const [menus, statuses] = await Promise.all([
    listMenusApi(),
    loadDictOptions("sys_common_status")
  ]);
  menuFlat.value = menus;
  if (statuses.length) {
    statusOptions.value = statuses;
  }
  await handleQuery();
});
</script>
