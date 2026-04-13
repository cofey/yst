<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="toolbar-main">
          <div class="query-grid">
            <el-input
              v-model="query.companyCode"
              class="query-field"
              placeholder="按单位编码搜索"
              clearable
              @keyup.enter="handleQuery"
            />
            <el-input
              v-model="query.companyName"
              class="query-field"
              placeholder="按单位名称搜索"
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
            <el-button
              v-hasPermi="['system:company:list']"
              class="toolbar-btn"
              type="primary"
              @click="handleQuery"
              >查询</el-button
            >
            <el-button class="toolbar-btn" @click="handleReset">重置</el-button>
          </div>
        </div>
      </div>

      <div class="table-tools">
        <el-button
          v-hasPermi="['system:company:add']"
          class="toolbar-btn"
          type="success"
          @click="openCreate"
          >新增</el-button
        >
        <el-upload
          v-hasPermi="['system:company:import']"
          :show-file-list="false"
          :before-upload="importExcel"
          accept=".xlsx,.xls"
        >
          <el-button class="toolbar-btn" type="warning">导入</el-button>
        </el-upload>
        <el-button
          v-hasPermi="['system:company:export']"
          class="toolbar-btn"
          type="info"
          @click="exportExcel"
          >导出</el-button
        >
      </div>

      <el-table :data="tableData" border empty-text="请设置条件后点击查询">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="companyCode" label="单位编码" min-width="160" />
        <el-table-column prop="companyName" label="单位名称" min-width="200" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <dict-tag :options="statusOptions" :value="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime, "minute") }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-hasPermi="['system:company:edit']"
              link
              type="primary"
              @click="openEdit(row)"
              >编辑</el-button
            >
            <el-button
              v-hasPermi="['system:company:remove']"
              link
              type="danger"
              @click="remove(row.companyId)"
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑单位' : '新增单位'" width="520px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="单位编码" required>
          <el-input v-model="form.companyCode" />
        </el-form-item>
        <el-form-item label="单位名称" required>
          <el-input v-model="form.companyName" />
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
  </div>
</template>

<script setup lang="ts">
import { getCurrentInstance, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { UploadRawFile } from "element-plus";
import {
  createCompanyApi,
  deleteCompanyApi,
  exportCompaniesApi,
  importCompaniesApi,
  listCompaniesApi,
  updateCompanyApi
} from "@/modules/system/company/api";
import { useAuthStore } from "@/stores/auth";
import type { CompanyItem } from "@/modules/system/company/types";
import { formatDateTime } from "@/shared/utils/datetime";

defineOptions({
  name: "SystemCompanyPage"
});

const authStore = useAuthStore();
const { proxy } = getCurrentInstance()!;
const { sys_common_status: statusOptions } = proxy.useDict("sys_common_status");

const query = reactive({
  companyCode: "",
  companyName: "",
  status: undefined as number | undefined
});
const queried = ref(false);

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const tableData = ref<CompanyItem[]>([]);
const dialogVisible = ref(false);
const editingId = ref<string | null>(null);
const form = reactive({
  companyCode: "",
  companyName: "",
  status: 1
});

const loadCompanyPage = async () => {
  const res = await listCompaniesApi({
    companyCode: query.companyCode || undefined,
    companyName: query.companyName || undefined,
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
  await loadCompanyPage();
};

const handleReset = async () => {
  query.companyCode = "";
  query.companyName = "";
  query.status = undefined;
  page.pageSize = 10;
  await handleQuery();
};

const handlePageNumChange = async (val: number) => {
  page.pageNum = val;
  if (!queried.value) {
    return;
  }
  await loadCompanyPage();
};

const handlePageSizeChange = async (val: number) => {
  page.pageSize = val;
  page.pageNum = 1;
  if (!queried.value) {
    return;
  }
  await loadCompanyPage();
};

const resetForm = () => {
  form.companyCode = "";
  form.companyName = "";
  form.status = 1;
};

const openCreate = () => {
  editingId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const openEdit = (row: CompanyItem) => {
  editingId.value = row.companyId;
  form.companyCode = row.companyCode;
  form.companyName = row.companyName;
  form.status = row.status;
  dialogVisible.value = true;
};

const submit = async () => {
  if (!form.companyCode || !form.companyName) {
    ElMessage.warning("请填写单位编码和名称");
    return;
  }
  if (editingId.value) {
    await updateCompanyApi(editingId.value, form);
  } else {
    await createCompanyApi(form);
  }
  ElMessage.success("操作成功");
  dialogVisible.value = false;
  if (queried.value) {
    await loadCompanyPage();
  }
};

const remove = async (companyId: string) => {
  await ElMessageBox.confirm("确认删除该单位吗？", "提示", { type: "warning" });
  await deleteCompanyApi(companyId);
  ElMessage.success("删除成功");
  if (queried.value) {
    await loadCompanyPage();
  }
};

const importExcel = async (file: UploadRawFile) => {
  await importCompaniesApi(file);
  ElMessage.success("导入成功");
  if (queried.value) {
    await loadCompanyPage();
  }
  return false;
};

const exportExcel = async () => {
  await exportCompaniesApi("单位列表.xlsx");
};

onMounted(async () => {
  if (!authStore.hasPermi("system:company:list")) {
    return;
  }
  await handleQuery();
});
</script>
