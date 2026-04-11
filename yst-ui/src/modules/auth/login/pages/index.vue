<template>
  <div class="login-page">
    <el-card class="login-card" shadow="hover">
      <template #header>YST 管理系统登录</template>
      <el-form :model="form" label-width="72px" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { authInfoApi, authMenusApi, loginApi } from "@/modules/auth/api";
import { useAuthStore } from "@/stores/auth";

defineOptions({
  name: "AuthLoginPage"
});

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const form = reactive({
  username: "admin",
  password: "123456"
});

const submit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning("请输入用户名和密码");
    return;
  }
  loading.value = true;
  try {
    const res = await loginApi(form);
    authStore.setAuth({
      token: res.token,
      userId: res.userId,
      username: res.username
    });
    const authInfo = await authInfoApi();
    const menus = await authMenusApi();
    authStore.setAuthz(authInfo.roles, authInfo.permissions);
    authStore.setMenus(menus);
    ElMessage.success("登录成功");
    await router.push("/home");
  } catch {
    authStore.clearAuth();
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(120deg, #f5f7fa, #e5eef9);
}

.login-card {
  width: min(92vw, 420px);
}

.tip {
  color: #909399;
  font-size: 12px;
}
</style>
