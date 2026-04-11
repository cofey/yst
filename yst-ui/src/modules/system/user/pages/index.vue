<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="toolbar-main">
          <div class="query-grid">
            <el-input
              v-model="query.username"
              class="query-field"
              placeholder="按用户名搜索"
              clearable
            />
            <el-input
              v-model="query.nickname"
              class="query-field"
              placeholder="按昵称搜索"
              clearable
            />
            <el-input
              v-model="query.phone"
              class="query-field"
              placeholder="按手机号搜索"
              clearable
            />
            <el-select
              v-model="query.companyId"
              class="query-field"
              clearable
              placeholder="按单位筛选"
            >
              <el-option
                v-for="company in companyOptions"
                :key="company.companyId"
                :label="company.companyName"
                :value="company.companyId"
              />
            </el-select>
            <el-select
              v-show="showMore"
              v-model="query.roleId"
              class="query-field"
              clearable
              placeholder="按角色筛选"
            >
              <el-option
                v-for="role in roleOptions"
                :key="role.roleId"
                :label="`${role.roleName}(${role.roleKey})`"
                :value="role.roleId"
              />
            </el-select>
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
            <el-button
              v-hasPermi="['system:user:list']"
              class="toolbar-btn"
              type="primary"
              @click="handleQuery"
              >查询</el-button
            >
            <el-button class="toolbar-btn" @click="handleReset">重置</el-button>
            <el-button class="toolbar-btn toolbar-btn-toggle" link type="primary" @click="showMore = !showMore">{{
              showMore ? "收起" : "展开"
            }}</el-button>
          </div>
        </div>
      </div>

      <div class="table-tools">
        <el-button v-hasPermi="['system:user:add']" class="toolbar-btn" type="success" @click="openCreate"
          >新增</el-button
        >
        <el-upload
          v-hasPermi="['system:user:import']"
          :show-file-list="false"
          :before-upload="importExcel"
          accept=".xlsx,.xls"
        >
          <el-button class="toolbar-btn" type="warning">导入</el-button>
        </el-upload>
        <el-button
          v-hasPermi="['system:user:export']"
          class="toolbar-btn"
          type="info"
          @click="exportExcel"
          >导出</el-button
        >
      </div>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column label="单位" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="name in row.companyNames || []" :key="name" size="small" class="mr8">{{
              name
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" min-width="180">
          <template #default="{ row }">
            <el-tag
              v-for="roleName in row.roleNames || []"
              :key="roleName"
              size="small"
              class="mr8"
              >{{ roleName }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime, "minute") }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-hasPermi="['system:user:edit']" link type="primary" @click="openEdit(row)"
              >编辑</el-button
            >
            <el-button
              v-hasPermi="['system:user:remove']"
              link
              type="danger"
              @click="remove(row.userId)"
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑用户' : '新增用户'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item v-if="!editingId" label="用户名" required>
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="所属单位">
          <el-select v-model="form.companyIds" style="width: 100%" multiple clearable>
            <el-option
              v-for="company in companyOptions"
              :key="company.companyId"
              :label="company.companyName"
              :value="company.companyId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" style="width: 100%" multiple clearable>
            <el-option
              v-for="role in roleOptions"
              :key="role.roleId"
              :label="`${role.roleName}(${role.roleKey})`"
              :value="role.roleId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="editingId ? '重置密码' : '初始密码'">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="不填则默认 123456"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import axios from "axios";
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { UploadRawFile } from "element-plus";
import { listCompaniesApi } from "@/modules/system/company/api";
import type { CompanyItem } from "@/modules/system/company/types";
import { listRolesApi } from "@/modules/system/role/api";
import type { RoleItem } from "@/modules/system/role/types";
import {
  createUserApi,
  deleteUserApi,
  listUsersApi,
  updateUserApi
} from "@/modules/system/user/api";
import type { UserItem } from "@/modules/system/user/types";
import type { DictOptionItem } from "@/modules/system/dict/types";
import { getDictLabel, getDictTagType, loadDictOptions } from "@/composables/useDict";
import { useAuthStore } from "@/stores/auth";
import { formatDateTime } from "@/shared/utils/datetime";

defineOptions({
  name: "SystemUserPage"
});

const authStore = useAuthStore();

const query = reactive({
  username: "",
  nickname: "",
  phone: "",
  companyId: undefined as string | undefined,
  roleId: undefined as string | undefined,
  status: undefined as number | undefined
});
const showMore = ref(false);
const queried = ref(false);

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const tableData = ref<UserItem[]>([]);
const companyOptions = ref<CompanyItem[]>([]);
const roleOptions = ref<RoleItem[]>([]);
const statusOptions = ref<DictOptionItem[]>([]);

const dialogVisible = ref(false);
const editingId = ref<string | null>(null);
const form = reactive({
  username: "",
  nickname: "",
  email: "",
  phone: "",
  companyIds: [] as string[],
  roleIds: [] as string[],
  status: 1,
  password: ""
});

const loadUsers = async () => {
  const username = query.username.trim();
  const nickname = query.nickname.trim();
  const phone = query.phone.trim();
  const res = await listUsersApi({
    username: username || undefined,
    nickname: nickname || undefined,
    phone: phone || undefined,
    companyId: query.companyId || undefined,
    roleId: query.roleId || undefined,
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
  await loadUsers();
};

const handleReset = async () => {
  query.username = "";
  query.nickname = "";
  query.phone = "";
  query.companyId = undefined;
  query.roleId = undefined;
  query.status = undefined;
  page.pageSize = 10;
  await handleQuery();
};

const handlePageNumChange = async (val: number) => {
  page.pageNum = val;
  if (!queried.value) {
    return;
  }
  await loadUsers();
};

const handlePageSizeChange = async (val: number) => {
  page.pageSize = val;
  page.pageNum = 1;
  if (!queried.value) {
    return;
  }
  await loadUsers();
};

const resetForm = () => {
  form.username = "";
  form.nickname = "";
  form.email = "";
  form.phone = "";
  form.companyIds = [];
  form.roleIds = [];
  form.status = 1;
  form.password = "";
};

const openCreate = () => {
  editingId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: UserItem) => {
  editingId.value = row.userId;
  form.username = row.username;
  form.nickname = row.nickname;
  form.email = row.email;
  form.phone = row.phone;
  form.companyIds = row.companyIds || [];
  form.roleIds = row.roleIds || [];
  form.status = row.status;
  form.password = "";
  dialogVisible.value = true;
};

const submit = async () => {
  if (!form.nickname || (!editingId.value && !form.username)) {
    ElMessage.warning("请填写必填项");
    return;
  }
  if (editingId.value) {
    await updateUserApi(editingId.value, {
      nickname: form.nickname,
      email: form.email,
      phone: form.phone,
      companyIds: form.companyIds,
      roleIds: form.roleIds,
      status: form.status,
      password: form.password || undefined
    });
  } else {
    await createUserApi({
      username: form.username,
      nickname: form.nickname,
      email: form.email,
      phone: form.phone,
      companyIds: form.companyIds,
      roleIds: form.roleIds,
      status: form.status,
      password: form.password || undefined
    });
  }
  ElMessage.success("操作成功");
  dialogVisible.value = false;
  if (queried.value) {
    await loadUsers();
  }
};

const remove = async (userId: string) => {
  await ElMessageBox.confirm("确认删除该用户吗？", "提示", { type: "warning" });
  await deleteUserApi(userId);
  ElMessage.success("删除成功");
  if (queried.value) {
    await loadUsers();
  }
};

const importExcel = async (file: UploadRawFile) => {
  const formData = new FormData();
  formData.append("file", file);
  await axios.post("/api/users/import", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
      Authorization: `Bearer ${authStore.token}`
    }
  });
  ElMessage.success("导入成功");
  if (queried.value) {
    await loadUsers();
  }
  return false;
};

const exportExcel = async () => {
  const res = await axios.get("/api/users/export", {
    responseType: "blob",
    headers: {
      Authorization: `Bearer ${authStore.token}`
    }
  });
  const blob = new Blob([res.data], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
  });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "用户列表.xlsx";
  a.click();
  window.URL.revokeObjectURL(url);
};

const loadOptions = async () => {
  const [companiesRes, rolesRes, statuses] = await Promise.all([
    listCompaniesApi({ pageNum: 1, pageSize: 1000 }),
    listRolesApi({ pageNum: 1, pageSize: 1000 }),
    loadDictOptions("sys_common_status")
  ]);
  companyOptions.value = companiesRes.records;
  roleOptions.value = rolesRes.records;
  if (statuses.length) {
    statusOptions.value = statuses;
  }
};

const statusLabel = (value: number) => getDictLabel(statusOptions.value, String(value), "-");

const statusTagType = (value: number) => getDictTagType(statusOptions.value, String(value));

onMounted(() => {
  if (authStore.hasPermi("system:user:list")) {
    loadOptions().then(() => {
      handleQuery();
    });
  }
});
</script>
