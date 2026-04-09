#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SONAR_DIR="${SCRIPT_DIR}/sonarqube"
SONAR_SCRIPT="${SONAR_DIR}/bin/macosx-universal-64/sonar.sh"

if [[ ! -x "${SONAR_SCRIPT}" ]]; then
  echo "未找到 SonarQube 启动脚本: ${SONAR_SCRIPT}" >&2
  exit 1
fi

JAVA17_HOME="$(/usr/libexec/java_home -v 17 2>/dev/null || true)"
if [[ -z "${JAVA17_HOME}" ]]; then
  echo "未检测到 Java 17，请先安装 JDK 17。" >&2
  exit 1
fi

cd "${SONAR_DIR}"
if SONAR_JAVA_PATH="${JAVA17_HOME}/bin/java" "${SONAR_SCRIPT}" status >/dev/null 2>&1; then
  echo "SonarQube 已在运行。"
  exit 0
fi

SONAR_JAVA_PATH="${JAVA17_HOME}/bin/java" "${SONAR_SCRIPT}" start
