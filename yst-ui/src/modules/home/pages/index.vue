<template>
  <div class="page">
    <el-card class="card">
      <h2>首页</h2>
    </el-card>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "HomePage"
});
</script>

<style scoped>
.page {
  min-height: calc(100vh - 64px);
  padding: 18px;
  background: #f5f7fa;
}

.card {
  min-height: 220px;
}
</style>
