import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import "element-plus/dist/index.css";
import App from "./App.vue";
import router from "./router";
import { setupHasPermiDirective } from "./directives/hasPermi";
import DictTag from "@/modules/system/dict/components/DictTag.vue";
import { setupDict } from "@/modules/system/dict/dict";
import "./styles/global.css";

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(ElementPlus, {
  locale: zhCn
});
setupHasPermiDirective(app);
setupDict(app);
app.component("DictTag", DictTag);
app.mount("#app");
