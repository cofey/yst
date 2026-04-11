-- 主表模板：业务键主键 + 审计字段
CREATE TABLE IF NOT EXISTS sys_user (
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '用户业务主键',
  username VARCHAR(64) NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  nickname VARCHAR(64) NOT NULL COMMENT '昵称',
  email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 关联表模板：业务键外键 + 联合唯一约束
CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '用户业务主键',
  role_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '角色业务主键',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_sys_user_role (user_id, role_id),
  KEY idx_sur_role_id (role_id),
  CONSTRAINT fk_sur_user_id FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_sur_role_id FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 外键策略
-- 1) 默认 RESTRICT，防止误删主记录
-- 2) 若需级联删除，必须在具体模块单独评审并记录

-- 无软删除下的唯一约束策略
-- 1) 业务唯一字段直接建 UNIQUE（例如 username）
-- 2) 删除前执行引用校验，存在引用则阻断
-- 3) 删除采用事务：先删关联，再删主表（按业务规则）
