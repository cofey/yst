<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="toolbar-main">
          <div class="query-grid">
            <el-input
              v-model="query.dictName"
              class="query-field"
              placeholder="按字典名称搜索"
              clearable
              @keyup.enter="handleQuery"
            />
            <el-input
              v-model="query.dictType"
              class="query-field"
              placeholder="按字典类型搜索"
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
              v-hasPermi="['system:dict:list']"
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
          v-hasPermi="['system:dict:add']"
          class="toolbar-btn"
          type="success"
          @click="openTypeCreate"
          >新增</el-button
        >
        <el-button
          v-hasPermi="['system:dict:edit']"
          class="toolbar-btn"
          type="warning"
          plain
          @click="clearAllCache"
          >清空缓存</el-button
        >
      </div>

      <el-table :data="dictTypeTable" border empty-text="暂无数据">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="dictName" label="字典名称" min-width="150" />
        <el-table-column prop="dictType" label="字典类型" min-width="180" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <dict-tag :options="statusOptions" :value="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime, "minute") }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              v-hasPermi="['system:dict:edit']"
              link
              type="primary"
              @click.stop="openTypeEdit(row)"
              >编辑</el-button
            >
            <el-button
              v-hasPermi="['system:dict:edit']"
              link
              type="warning"
              @click.stop="clearTypeCache(row.dictType)"
              >清缓存</el-button
            >
            <el-button
              v-hasPermi="['system:dict:list']"
              link
              type="warning"
              @click.stop="goDataPage(row)"
              >数据</el-button
            >
            <el-button
              v-hasPermi="['system:dict:remove']"
              link
              type="danger"
              @click.stop="removeType(row.dictId)"
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

    <el-dialog
      v-model="typeDialogVisible"
      :title="editingTypeId ? '编辑字典类型' : '新增字典类型'"
      width="500px"
    >
      <el-form :model="typeForm" label-width="96px">
        <el-form-item label="字典名称" required>
          <el-input v-model="typeForm.dictName" />
        </el-form-item>
        <el-form-item label="字典类型" required>
          <el-input v-model="typeForm.dictType" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="typeForm.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="typeForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitType">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { getCurrentInstance, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import {
  clearAllDictCacheApi,
  clearDictTypeCacheApi,
  createDictTypeApi,
  deleteDictTypeApi,
  listDictTypesApi,
  updateDictTypeApi
} from "@/modules/system/dict/api";
import { useAuthStore } from "@/stores/auth";
import { clearDictCache } from "@/modules/system/dict/dict";
import type { DictTypeItem } from "@/modules/system/dict/types";
import { formatDateTime } from "@/shared/utils/datetime";

defineOptions({
  name: "SystemDictTypePage"
});

const router = useRouter();
const authStore = useAuthStore();
const { proxy } = getCurrentInstance()!;
const { sys_common_status: statusOptions } = proxy.useDict("sys_common_status");

const query = reactive({
  dictName: "",
  dictType: "",
  status: undefined as number | undefined
});
const queried = ref(false);

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const dictTypeTable = ref<DictTypeItem[]>([]);

const typeDialogVisible = ref(false);
const editingTypeId = ref<string | null>(null);
const typeForm = reactive({
  dictName: "",
  dictType: "",
  status: 1,
  remark: ""
});

const loadTypes = async () => {
  const res = await listDictTypesApi({
    dictName: query.dictName || undefined,
    dictType: query.dictType || undefined,
    status: query.status,
    pageNum: page.pageNum,
    pageSize: page.pageSize
  });
  dictTypeTable.value = res.records;
  page.total = res.total;
};

const handleQuery = async () => {
  page.pageNum = 1;
  queried.value = true;
  await loadTypes();
};

const handleReset = async () => {
  query.dictName = "";
  query.dictType = "";
  query.status = undefined;
  page.pageSize = 10;
  await handleQuery();
};

const handlePageNumChange = async (val: number) => {
  page.pageNum = val;
  if (!queried.value) {
    return;
  }
  await loadTypes();
};

const handlePageSizeChange = async (val: number) => {
  page.pageSize = val;
  page.pageNum = 1;
  if (!queried.value) {
    return;
  }
  await loadTypes();
};

const resetTypeForm = () => {
  typeForm.dictName = "";
  typeForm.dictType = "";
  typeForm.status = 1;
  typeForm.remark = "";
};

const openTypeCreate = () => {
  editingTypeId.value = null;
  resetTypeForm();
  typeDialogVisible.value = true;
};

const openTypeEdit = (row: DictTypeItem) => {
  editingTypeId.value = row.dictId;
  typeForm.dictName = row.dictName;
  typeForm.dictType = row.dictType;
  typeForm.status = row.status;
  typeForm.remark = row.remark || "";
  typeDialogVisible.value = true;
};

const submitType = async () => {
  if (!typeForm.dictName || !typeForm.dictType) {
    ElMessage.warning("请填写字典名称和字典类型");
    return;
  }
  if (editingTypeId.value) {
    await updateDictTypeApi(editingTypeId.value, typeForm);
  } else {
    await createDictTypeApi(typeForm);
  }
  clearDictCache(typeForm.dictType);
  ElMessage.success("操作成功");
  typeDialogVisible.value = false;
  if (queried.value) {
    await loadTypes();
  }
};

const removeType = async (dictId: string) => {
  await ElMessageBox.confirm("确认删除该字典类型吗？", "提示", { type: "warning" });
  await deleteDictTypeApi(dictId);
  ElMessage.success("删除成功");
  if (queried.value) {
    await loadTypes();
  }
};

const clearTypeCache = async (dictType: string) => {
  await clearDictTypeCacheApi(dictType);
  clearDictCache(dictType);
  ElMessage.success(`已清理字典缓存：${dictType}`);
};

const clearAllCache = async () => {
  await ElMessageBox.confirm("确认清空全部字典缓存吗？", "提示", { type: "warning" });
  await clearAllDictCacheApi();
  clearDictCache();
  ElMessage.success("已清理全部字典缓存");
};

const goDataPage = (row: DictTypeItem) => {
  router.push({
    name: "dict-data",
    params: { dictType: row.dictType },
    query: { dictName: row.dictName }
  });
};

onMounted(async () => {
  if (!authStore.hasPermi("system:dict:list")) {
    return;
  }
  await handleQuery();
});
</script>
