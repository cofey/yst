import type { App, DirectiveBinding } from "vue";
import { useAuthStore } from "@/stores/auth";

function removeEl(el: HTMLElement) {
  if (el.parentNode) {
    el.parentNode.removeChild(el);
  }
}

function checkPermission(el: HTMLElement, binding: DirectiveBinding<string[]>) {
  const authStore = useAuthStore();
  const required = binding.value;
  if (!required || required.length === 0) {
    return;
  }
  const hasPermission = required.some((permission) => authStore.hasPermi(permission));
  if (!hasPermission) {
    removeEl(el);
  }
}

export function setupHasPermiDirective(app: App) {
  app.directive("hasPermi", {
    mounted(el: HTMLElement, binding: DirectiveBinding<string[]>) {
      checkPermission(el, binding);
    }
  });
}
