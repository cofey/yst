<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <div class="toolbar-main">
          <div class="query-grid">
            <el-input
              v-model="query.dictLabel"
              class="query-field"
              placeholder="按字典标签搜索"
              clearable
              @keyup.enter="handleQuery"
            />
            <el-select
              v-show="showMore"
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
            <el-button v-hasPermi="['system:dict:list']" class="toolbar-btn" type="primary" @click="handleQuery"
              >查询</el-button
            >
            <el-button class="toolbar-btn" @click="handleReset">重置</el-button>
            <el-button class="toolbar-btn toolbar-btn-toggle" link type="primary" @click="showMore = !showMore">{{
              showMore ? "收起" : "展开"
            }}</el-button>
          </div>
        </div>
      </div>

      <div class="table-tools dict-tools">
        <span class="dict-title-inline">字典数据维护 {{ pageTitle }}</span>
        <div class="dict-tools-actions">
          <el-button class="toolbar-btn" type="default" @click="goBack">返回类型</el-button>
          <el-button
            v-hasPermi="['system:dict:edit']"
            class="toolbar-btn"
            type="warning"
            plain
            :disabled="!currentDictType"
            @click="clearCurrentTypeCache"
            >清当前类型缓存</el-button
          >
          <el-button
            v-hasPermi="['system:dict:edit']"
            class="toolbar-btn"
            type="warning"
            plain
            @click="clearAllCache"
            >清空缓存</el-button
          >
          <el-button
            v-hasPermi="['system:dict:add']"
            class="toolbar-btn"
            type="success"
            @click="openDataCreate"
            >新增</el-button
          >
        </div>
      </div>

      <el-table :data="dictDataTable" border empty-text="暂无数据">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="dictLabel" label="标签" min-width="130" />
        <el-table-column prop="dictValue" label="键值" min-width="120" />
        <el-table-column prop="dictSort" label="排序" width="80" />
        <el-table-column prop="listClass" label="回显样式" min-width="100" />
        <el-table-column label="默认" width="70">
          <template #default="{ row }">{{ row.isDefault === "Y" ? "是" : "否" }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime, "minute") }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button
              v-hasPermi="['system:dict:edit']"
              link
              type="primary"
              @click="openDataEdit(row)"
              >编辑</el-button
            >
            <el-button
              v-hasPermi="['system:dict:remove']"
              link
              type="danger"
              @click="removeData(row.dictCode)"
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
      v-model="dataDialogVisible"
      :title="editingDataCode ? '编辑字典数据' : '新增字典数据'"
      width="560px"
    >
      <el-form :model="dataForm" label-width="96px">
        <el-form-item label="字典类型" required>
          <el-input v-model="dataForm.dictType" disabled />
        </el-form-item>
        <el-form-item label="字典标签" required>
          <el-input v-model="dataForm.dictLabel" />
        </el-form-item>
        <el-form-item label="字典键值" required>
          <el-input v-model="dataForm.dictValue" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dataForm.dictSort" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="回显样式">
          <el-input v-model="dataForm.listClass" />
        </el-form-item>
        <el-form-item label="默认">
          <el-radio-group v-model="dataForm.isDefault">
            <el-radio value="Y">是</el-radio>
            <el-radio value="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dataForm.status">
            <el-radio v-for="item in statusOptions" :key="item.value" :value="Number(item.value)">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dataForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitData">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import {
  clearAllDictCacheApi,
  clearDictTypeCacheApi,
  createDictDataApi,
  deleteDictDataApi,
  listDictDataApi,
  updateDictDataApi
} from "@/modules/system/dict/api";
import { useAuthStore } from "@/stores/auth";
import {
  clearDictCache,
  getDictLabel,
  getDictTagType,
  loadDictOptions
} from "@/composables/useDict";
import type { DictDataItem, DictOptionItem } from "@/modules/system/dict/types";
import { formatDateTime } from "@/shared/utils/datetime";

defineOptions({
  name: "SystemDictDataPage"
});

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const currentDictType = computed(() => String(route.params.dictType || ""));
const pageTitle = computed(() => {
  const name = route.query.dictName ? String(route.query.dictName) : "";
  return name ? `(${name})` : `(${currentDictType.value})`;
});

const query = reactive({
  dictLabel: "",
  status: undefined as number | undefined
});
const showMore = ref(false);
const queried = ref(false);

const page = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const dictDataTable = ref<DictDataItem[]>([]);
const statusOptions = ref<DictOptionItem[]>([]);
const dataDialogVisible = ref(false);
const editingDataCode = ref<string | null>(null);
const dataForm = reactive({
  dictSort: 0,
  dictLabel: "",
  dictValue: "",
  dictType: "",
  cssClass: "",
  listClass: "",
  isDefault: "N" as "Y" | "N",
  status: 1,
  remark: ""
});

const loadData = async () => {
  if (!currentDictType.value) {
    dictDataTable.value = [];
    page.total = 0;
    return;
  }
  const res = await listDictDataApi({
    dictType: currentDictType.value,
    dictLabel: query.dictLabel || undefined,
    status: query.status,
    pageNum: page.pageNum,
    pageSize: page.pageSize
  });
  dictDataTable.value = res.records;
  page.total = res.total;
};

const handleQuery = async () => {
  page.pageNum = 1;
  queried.value = true;
  await loadData();
};

const handleReset = async () => {
  query.dictLabel = "";
  query.status = undefined;
  page.pageSize = 10;
  await handleQuery();
};

const handlePageNumChange = async (val: number) => {
  page.pageNum = val;
  if (!queried.value) {
    return;
  }
  await loadData();
};

const handlePageSizeChange = async (val: number) => {
  page.pageSize = val;
  page.pageNum = 1;
  if (!queried.value) {
    return;
  }
  await loadData();
};

const resetDataForm = () => {
  dataForm.dictSort = 0;
  dataForm.dictLabel = "";
  dataForm.dictValue = "";
  dataForm.dictType = currentDictType.value;
  dataForm.cssClass = "";
  dataForm.listClass = "";
  dataForm.isDefault = "N";
  dataForm.status = 1;
  dataForm.remark = "";
};

const openDataCreate = () => {
  editingDataCode.value = null;
  resetDataForm();
  dataDialogVisible.value = true;
};

const openDataEdit = (row: DictDataItem) => {
  editingDataCode.value = row.dictCode;
  dataForm.dictSort = row.dictSort;
  dataForm.dictLabel = row.dictLabel;
  dataForm.dictValue = row.dictValue;
  dataForm.dictType = row.dictType;
  dataForm.cssClass = row.cssClass || "";
  dataForm.listClass = row.listClass || "";
  dataForm.isDefault = row.isDefault;
  dataForm.status = row.status;
  dataForm.remark = row.remark || "";
  dataDialogVisible.value = true;
};

const submitData = async () => {
  if (!dataForm.dictType || !dataForm.dictLabel || !dataForm.dictValue) {
    ElMessage.warning("请填写字典类型、标签和键值");
    return;
  }
  if (editingDataCode.value) {
    await updateDictDataApi(editingDataCode.value, dataForm);
  } else {
    await createDictDataApi(dataForm);
  }
  clearDictCache(dataForm.dictType);
  ElMessage.success("操作成功");
  dataDialogVisible.value = false;
  if (queried.value) {
    await loadData();
  }
};

const removeData = async (dictCode: string) => {
  await ElMessageBox.confirm("确认删除该字典数据吗？", "提示", { type: "warning" });
  await deleteDictDataApi(dictCode);
  ElMessage.success("删除成功");
  if (queried.value) {
    await loadData();
  }
};

const goBack = () => {
  router.push("/system/dicts");
};

const clearCurrentTypeCache = async () => {
  if (!currentDictType.value) {
    ElMessage.warning("当前字典类型为空");
    return;
  }
  await clearDictTypeCacheApi(currentDictType.value);
  clearDictCache(currentDictType.value);
  ElMessage.success(`已清理字典缓存：${currentDictType.value}`);
};

const clearAllCache = async () => {
  await ElMessageBox.confirm("确认清空全部字典缓存吗？", "提示", { type: "warning" });
  await clearAllDictCacheApi();
  clearDictCache();
  ElMessage.success("已清理全部字典缓存");
};

const statusLabel = (value: number) => getDictLabel(statusOptions.value, String(value), "-");

const statusTagType = (value: number) => getDictTagType(statusOptions.value, String(value));

watch(
  () => currentDictType.value,
  () => {
    void handleReset();
    resetDataForm();
  }
);

onMounted(async () => {
  if (!authStore.hasPermi("system:dict:list")) {
    return;
  }
  const statuses = await loadDictOptions("sys_common_status");
  if (statuses.length) {
    statusOptions.value = statuses;
  }
  resetDataForm();
  await handleQuery();
});
</script>

<style scoped>
.dict-tools {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.dict-title-inline {
  color: #909399;
  font-size: 13px;
  line-height: 1;
}

.dict-tools-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .dict-tools {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
