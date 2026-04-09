<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="left">
          <el-input
            v-model="keyword"
            placeholder="按用户名/昵称/手机号搜索"
            clearable
            @keyup.enter="loadUsers"
            @clear="loadUsers"
          />
          <el-button type="primary" @click="loadUsers">查询</el-button>
          <el-button type="success" @click="openCreate">新增</el-button>
          <el-upload :show-file-list="false" :before-upload="importExcel" accept=".xlsx,.xls">
            <el-button type="warning">导入</el-button>
          </el-upload>
          <el-button type="info" @click="exportExcel">导出</el-button>
        </div>
      </div>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{
              row.status === 1 ? "启用" : "禁用"
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑用户' : '新增用户'" width="520px">
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
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
import { createUserApi, deleteUserApi, listUsersApi, updateUserApi } from "@/api/user";
import { useAuthStore } from "@/stores/auth";
import type { UserItem } from "@/types";

const authStore = useAuthStore();

const keyword = ref("");
const tableData = ref<UserItem[]>([]);
const dialogVisible = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  username: "",
  nickname: "",
  email: "",
  phone: "",
  status: 1,
  password: ""
});

const resetForm = () => {
  form.username = "";
  form.nickname = "";
  form.email = "";
  form.phone = "";
  form.status = 1;
  form.password = "";
};

const loadUsers = async () => {
  const res = await listUsersApi(keyword.value);
  tableData.value = res;
};

const openCreate = () => {
  editingId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: UserItem) => {
  editingId.value = row.id;
  form.username = row.username;
  form.nickname = row.nickname;
  form.email = row.email;
  form.phone = row.phone;
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
      status: form.status,
      password: form.password || undefined
    });
  } else {
    await createUserApi({
      username: form.username,
      nickname: form.nickname,
      email: form.email,
      phone: form.phone,
      status: form.status,
      password: form.password || undefined
    });
  }
  ElMessage.success("操作成功");
  dialogVisible.value = false;
  await loadUsers();
};

const remove = async (id: number) => {
  await ElMessageBox.confirm("确认删除该用户吗？", "提示", { type: "warning" });
  await deleteUserApi(id);
  ElMessage.success("删除成功");
  await loadUsers();
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
  await loadUsers();
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

onMounted(() => {
  loadUsers();
});
</script>

<style scoped>
.page {
  min-height: calc(100vh - 64px);
  padding: 18px;
  background: #f5f7fa;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  gap: 12px;
}

.left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.left .el-input {
  width: 300px;
}
</style>
