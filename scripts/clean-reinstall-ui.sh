#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
UI_DIR="$ROOT_DIR/yst-ui"
REQUIRED_NODE="v20.15.0"
REQUIRED_PNPM="9.15.9"
NPM_REGISTRY="${NPM_REGISTRY:-https://registry.npmjs.org}"

ensure_pnpm() {
  if command -v corepack >/dev/null 2>&1; then
    corepack disable pnpm >/dev/null 2>&1 || true
    hash -r || true
  fi

  echo "安装/覆盖 pnpm@${REQUIRED_PNPM}（绕过 corepack 签名校验）..."
  if ! npm install -g "pnpm@${REQUIRED_PNPM}" >/dev/null 2>&1; then
    mkdir -p "$HOME/.npm-global"
    npm install -g "pnpm@${REQUIRED_PNPM}" --prefix "$HOME/.npm-global" >/dev/null 2>&1 || true
    export PATH="$HOME/.npm-global/bin:$PATH"
  fi
  hash -r || true

  if ! command -v pnpm >/dev/null 2>&1; then
    NPM_PREFIX="$(npm config get prefix 2>/dev/null || true)"
    if [ -n "${NPM_PREFIX:-}" ] && [ -d "$NPM_PREFIX/bin" ]; then
      export PATH="$NPM_PREFIX/bin:$PATH"
      hash -r || true
    fi
  fi

  if ! command -v pnpm >/dev/null 2>&1 && [ -d "$HOME/.npm-global/bin" ]; then
    export PATH="$HOME/.npm-global/bin:$PATH"
    hash -r || true
  fi

  if ! command -v pnpm >/dev/null 2>&1; then
    echo "仍未检测到 pnpm，请手动执行：npm install -g pnpm@${REQUIRED_PNPM}" >&2
    exit 1
  fi

  local current_pnpm
  current_pnpm="$(pnpm --version)"
  if [ "$current_pnpm" != "${REQUIRED_PNPM}" ]; then
    echo "pnpm 版本不匹配: 当前 $current_pnpm，要求 ${REQUIRED_PNPM}" >&2
    exit 1
  fi
}

if [ ! -d "$UI_DIR" ]; then
  echo "前端目录不存在: $UI_DIR" >&2
  exit 1
fi

cd "$UI_DIR"

echo "[1/5] 切换 Node 版本到 $REQUIRED_NODE"
if [ -s "$HOME/.nvm/nvm.sh" ]; then
  # shellcheck disable=SC1090
  . "$HOME/.nvm/nvm.sh"
  nvm use "$REQUIRED_NODE" >/dev/null
else
  echo "未找到 nvm，请先安装 nvm 或手动切换到 $REQUIRED_NODE" >&2
  exit 1
fi

if [ "$(node -v)" != "$REQUIRED_NODE" ]; then
  echo "Node 版本不匹配: 当前 $(node -v)，要求 $REQUIRED_NODE" >&2
  exit 1
fi

ensure_pnpm

echo "[2/5] 校验 pnpm 版本"
echo "当前 pnpm 版本: $(pnpm --version)"

echo "[3/5] 清理 node_modules 和 dist"
rm -rf node_modules
rm -rf dist

echo "[4/5] 清理 pnpm 缓存元数据"
pnpm store prune >/dev/null || true

echo "[5/5] 重新安装依赖并构建"
pnpm install --registry "${NPM_REGISTRY}"
pnpm run build

echo "完成: 已按指定 Node/pnpm 清理、重装并重新构建"
