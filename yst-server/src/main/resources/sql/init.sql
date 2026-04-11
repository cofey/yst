CREATE DATABASE IF NOT EXISTS yst DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE yst;

CREATE TABLE IF NOT EXISTS sys_company (
  company_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '单位ID',
  company_code VARCHAR(64) NOT NULL COMMENT '单位编码',
  company_name VARCHAR(100) NOT NULL COMMENT '单位名称',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (company_id),
  UNIQUE KEY uk_company_code (company_code),
  UNIQUE KEY uk_company_name (company_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单位表';

CREATE TABLE IF NOT EXISTS sys_role (
  role_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '角色ID',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  role_key VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (role_id),
  UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

CREATE TABLE IF NOT EXISTS sys_menu (
  menu_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '菜单ID',
  parent_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin DEFAULT NULL COMMENT '父菜单ID',
  menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
  menu_type CHAR(1) NOT NULL COMMENT '菜单类型 M目录 C菜单 F按钮',
  path VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
  component VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  icon VARCHAR(100) DEFAULT NULL COMMENT '图标',
  perms VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  visible TINYINT NOT NULL DEFAULT 1 COMMENT '显示状态 1显示 0隐藏',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (menu_id),
  KEY idx_parent_id (parent_id),
  KEY idx_perms (perms),
  CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES sys_menu(menu_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE IF NOT EXISTS sys_user (
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '用户ID',
  username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
  nickname VARCHAR(64) NOT NULL COMMENT '昵称',
  email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_user_role (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT (UUID()) COMMENT '主键ID',
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '用户ID',
  role_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '角色ID',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role_user_role (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

CREATE TABLE IF NOT EXISTS sys_role_menu (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT (UUID()) COMMENT '主键ID',
  role_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '角色ID',
  menu_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_menu_role_menu (role_id, menu_id),
  CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(menu_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

CREATE TABLE IF NOT EXISTS sys_user_company (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT (UUID()) COMMENT '主键ID',
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '用户ID',
  company_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '单位ID',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_company_user_company (user_id, company_id),
  CONSTRAINT fk_user_company_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_user_company_company FOREIGN KEY (company_id) REFERENCES sys_company(company_id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和单位关联表';

CREATE TABLE IF NOT EXISTS sys_dict_type (
  dict_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '字典主键',
  dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
  dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (dict_id),
  UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE IF NOT EXISTS sys_dict_data (
  dict_code CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '字典编码',
  dict_sort INT NOT NULL DEFAULT 0 COMMENT '字典排序',
  dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
  dict_value VARCHAR(100) NOT NULL COMMENT '字典键值',
  dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
  css_class VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
  list_class VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
  is_default CHAR(1) NOT NULL DEFAULT 'N' COMMENT '是否默认 Y是 N否',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (dict_code),
  KEY idx_dict_type (dict_type),
  UNIQUE KEY uk_dict_type_value (dict_type, dict_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

INSERT INTO sys_company (company_id, company_code, company_name, status, create_time, update_time)
VALUES ('a5fda963-45d3-49e8-bca3-9237f2784b70', 'HQ', '默认单位', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  company_code = VALUES(company_code),
  company_name = VALUES(company_name),
  status = VALUES(status),
  update_time = VALUES(update_time);

INSERT INTO sys_role (role_id, role_name, role_key, status, create_time, update_time)
VALUES ('0785700a-ec07-476f-b610-4488e2279be0', '超级管理员', 'admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  role_key = VALUES(role_key),
  status = VALUES(status),
  update_time = VALUES(update_time);

INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, path, component, icon, perms, visible, status, sort, create_time, update_time)
VALUES
  ('c04e73d1-4f8a-4d90-a461-57d41b9be21a', NULL, '系统管理', 'C', '/system', NULL, 'Setting', NULL, 1, 1, 1, NOW(), NOW()),
  ('bb9baede-6f5e-4078-a624-5757f6a1ad95', 'c04e73d1-4f8a-4d90-a461-57d41b9be21a', '用户管理', 'C', '/system/users', 'modules/system/user/pages/index.vue', 'UserFilled', 'system:user:view', 1, 1, 1, NOW(), NOW()),
  ('9b59c039-d2d3-4540-9d5c-cf763f20359b', 'c04e73d1-4f8a-4d90-a461-57d41b9be21a', '角色管理', 'C', '/system/roles', 'modules/system/role/pages/index.vue', 'Avatar', 'system:role:view', 1, 1, 2, NOW(), NOW()),
  ('f7f46292-e38f-43e0-8814-7af49c36ca56', 'c04e73d1-4f8a-4d90-a461-57d41b9be21a', '菜单管理', 'C', '/system/menus', 'modules/system/menu/pages/index.vue', 'Menu', 'system:menu:view', 1, 1, 3, NOW(), NOW()),
  ('d30980be-d6f2-4b98-a794-a5e3529c3a37', 'c04e73d1-4f8a-4d90-a461-57d41b9be21a', '单位管理', 'C', '/system/companies', 'modules/system/company/pages/index.vue', 'OfficeBuilding', 'system:company:view', 1, 1, 4, NOW(), NOW()),
  ('958a02d8-9fd4-41b7-a2ed-18d994f26c72', 'c04e73d1-4f8a-4d90-a461-57d41b9be21a', '字典管理', 'C', '/system/dicts', 'modules/system/dict/pages/index.vue', 'CollectionTag', 'system:dict:view', 1, 1, 5, NOW(), NOW()),
  ('e9f6b8df-e89c-43c7-bb2b-0ce89ac2f001', 'bb9baede-6f5e-4078-a624-5757f6a1ad95', '用户查询', 'F', NULL, NULL, NULL, 'system:user:list', 1, 1, 1, NOW(), NOW()),
  ('e9f6b8df-e89c-43c7-bb2b-0ce89ac2f002', 'bb9baede-6f5e-4078-a624-5757f6a1ad95', '用户新增', 'F', NULL, NULL, NULL, 'system:user:add', 1, 1, 2, NOW(), NOW()),
  ('e9f6b8df-e89c-43c7-bb2b-0ce89ac2f003', 'bb9baede-6f5e-4078-a624-5757f6a1ad95', '用户修改', 'F', NULL, NULL, NULL, 'system:user:edit', 1, 1, 3, NOW(), NOW()),
  ('e9f6b8df-e89c-43c7-bb2b-0ce89ac2f004', 'bb9baede-6f5e-4078-a624-5757f6a1ad95', '用户删除', 'F', NULL, NULL, NULL, 'system:user:remove', 1, 1, 4, NOW(), NOW()),
  ('e9f6b8df-e89c-43c7-bb2b-0ce89ac2f005', 'bb9baede-6f5e-4078-a624-5757f6a1ad95', '用户导入', 'F', NULL, NULL, NULL, 'system:user:import', 1, 1, 5, NOW(), NOW()),
  ('e9f6b8df-e89c-43c7-bb2b-0ce89ac2f006', 'bb9baede-6f5e-4078-a624-5757f6a1ad95', '用户导出', 'F', NULL, NULL, NULL, 'system:user:export', 1, 1, 6, NOW(), NOW()),
  ('b16dbf6e-7dbd-4f8f-b75f-7746e5a9a001', '9b59c039-d2d3-4540-9d5c-cf763f20359b', '角色查询', 'F', NULL, NULL, NULL, 'system:role:list', 1, 1, 1, NOW(), NOW()),
  ('b16dbf6e-7dbd-4f8f-b75f-7746e5a9a002', '9b59c039-d2d3-4540-9d5c-cf763f20359b', '角色新增', 'F', NULL, NULL, NULL, 'system:role:add', 1, 1, 2, NOW(), NOW()),
  ('b16dbf6e-7dbd-4f8f-b75f-7746e5a9a003', '9b59c039-d2d3-4540-9d5c-cf763f20359b', '角色修改', 'F', NULL, NULL, NULL, 'system:role:edit', 1, 1, 3, NOW(), NOW()),
  ('b16dbf6e-7dbd-4f8f-b75f-7746e5a9a004', '9b59c039-d2d3-4540-9d5c-cf763f20359b', '角色删除', 'F', NULL, NULL, NULL, 'system:role:remove', 1, 1, 4, NOW(), NOW()),
  ('c2d323b5-37ec-4ad4-8f7b-1f6ce13ae001', 'f7f46292-e38f-43e0-8814-7af49c36ca56', '菜单查询', 'F', NULL, NULL, NULL, 'system:menu:list', 1, 1, 1, NOW(), NOW()),
  ('c2d323b5-37ec-4ad4-8f7b-1f6ce13ae002', 'f7f46292-e38f-43e0-8814-7af49c36ca56', '菜单新增', 'F', NULL, NULL, NULL, 'system:menu:add', 1, 1, 2, NOW(), NOW()),
  ('c2d323b5-37ec-4ad4-8f7b-1f6ce13ae003', 'f7f46292-e38f-43e0-8814-7af49c36ca56', '菜单修改', 'F', NULL, NULL, NULL, 'system:menu:edit', 1, 1, 3, NOW(), NOW()),
  ('c2d323b5-37ec-4ad4-8f7b-1f6ce13ae004', 'f7f46292-e38f-43e0-8814-7af49c36ca56', '菜单删除', 'F', NULL, NULL, NULL, 'system:menu:remove', 1, 1, 4, NOW(), NOW()),
  ('d439b7e9-0410-4843-8dd5-19316f7a4001', 'd30980be-d6f2-4b98-a794-a5e3529c3a37', '单位查询', 'F', NULL, NULL, NULL, 'system:company:list', 1, 1, 1, NOW(), NOW()),
  ('d439b7e9-0410-4843-8dd5-19316f7a4002', 'd30980be-d6f2-4b98-a794-a5e3529c3a37', '单位新增', 'F', NULL, NULL, NULL, 'system:company:add', 1, 1, 2, NOW(), NOW()),
  ('d439b7e9-0410-4843-8dd5-19316f7a4003', 'd30980be-d6f2-4b98-a794-a5e3529c3a37', '单位修改', 'F', NULL, NULL, NULL, 'system:company:edit', 1, 1, 3, NOW(), NOW()),
  ('d439b7e9-0410-4843-8dd5-19316f7a4004', 'd30980be-d6f2-4b98-a794-a5e3529c3a37', '单位删除', 'F', NULL, NULL, NULL, 'system:company:remove', 1, 1, 4, NOW(), NOW()),
  ('f57e54d2-2f04-4e96-b715-19aa7fdcd001', '958a02d8-9fd4-41b7-a2ed-18d994f26c72', '字典查询', 'F', NULL, NULL, NULL, 'system:dict:list', 1, 1, 1, NOW(), NOW()),
  ('f57e54d2-2f04-4e96-b715-19aa7fdcd002', '958a02d8-9fd4-41b7-a2ed-18d994f26c72', '字典新增', 'F', NULL, NULL, NULL, 'system:dict:add', 1, 1, 2, NOW(), NOW()),
  ('f57e54d2-2f04-4e96-b715-19aa7fdcd003', '958a02d8-9fd4-41b7-a2ed-18d994f26c72', '字典修改', 'F', NULL, NULL, NULL, 'system:dict:edit', 1, 1, 3, NOW(), NOW()),
  ('f57e54d2-2f04-4e96-b715-19aa7fdcd004', '958a02d8-9fd4-41b7-a2ed-18d994f26c72', '字典删除', 'F', NULL, NULL, NULL, 'system:dict:remove', 1, 1, 4, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  parent_id = VALUES(parent_id),
  menu_name = VALUES(menu_name),
  menu_type = VALUES(menu_type),
  path = VALUES(path),
  component = VALUES(component),
  icon = VALUES(icon),
  perms = VALUES(perms),
  visible = VALUES(visible),
  status = VALUES(status),
  sort = VALUES(sort),
  update_time = VALUES(update_time);

INSERT INTO sys_user (user_id, username, password, nickname, email, phone, status, create_time, update_time)
VALUES ('04ef0dcf-e0be-4468-ac9c-74eab53ac625', 'admin', '$2y$10$n7pf1Riy2XwyPQtklk2FJuolS6YpRM/4sOFVe04Mwi3GI8ayIl6AO', '超级管理员', 'admin@yst.com', '13800000000', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  nickname = VALUES(nickname),
  email = VALUES(email),
  phone = VALUES(phone),
  status = VALUES(status),
  update_time = VALUES(update_time);

INSERT INTO sys_user_role (user_id, role_id)
VALUES ('04ef0dcf-e0be-4468-ac9c-74eab53ac625', '0785700a-ec07-476f-b610-4488e2279be0')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO sys_user_company (user_id, company_id)
VALUES ('04ef0dcf-e0be-4468-ac9c-74eab53ac625', 'a5fda963-45d3-49e8-bca3-9237f2784b70')
ON DUPLICATE KEY UPDATE company_id = VALUES(company_id);

INSERT INTO sys_role_menu (role_id, menu_id)
VALUES
  ('0785700a-ec07-476f-b610-4488e2279be0', 'c04e73d1-4f8a-4d90-a461-57d41b9be21a'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'bb9baede-6f5e-4078-a624-5757f6a1ad95'),
  ('0785700a-ec07-476f-b610-4488e2279be0', '9b59c039-d2d3-4540-9d5c-cf763f20359b'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'f7f46292-e38f-43e0-8814-7af49c36ca56'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'd30980be-d6f2-4b98-a794-a5e3529c3a37'),
  ('0785700a-ec07-476f-b610-4488e2279be0', '958a02d8-9fd4-41b7-a2ed-18d994f26c72'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'e9f6b8df-e89c-43c7-bb2b-0ce89ac2f001'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'e9f6b8df-e89c-43c7-bb2b-0ce89ac2f002'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'e9f6b8df-e89c-43c7-bb2b-0ce89ac2f003'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'e9f6b8df-e89c-43c7-bb2b-0ce89ac2f004'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'e9f6b8df-e89c-43c7-bb2b-0ce89ac2f005'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'e9f6b8df-e89c-43c7-bb2b-0ce89ac2f006'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'b16dbf6e-7dbd-4f8f-b75f-7746e5a9a001'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'b16dbf6e-7dbd-4f8f-b75f-7746e5a9a002'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'b16dbf6e-7dbd-4f8f-b75f-7746e5a9a003'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'b16dbf6e-7dbd-4f8f-b75f-7746e5a9a004'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'c2d323b5-37ec-4ad4-8f7b-1f6ce13ae001'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'c2d323b5-37ec-4ad4-8f7b-1f6ce13ae002'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'c2d323b5-37ec-4ad4-8f7b-1f6ce13ae003'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'c2d323b5-37ec-4ad4-8f7b-1f6ce13ae004'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'd439b7e9-0410-4843-8dd5-19316f7a4001'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'd439b7e9-0410-4843-8dd5-19316f7a4002'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'd439b7e9-0410-4843-8dd5-19316f7a4003'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'd439b7e9-0410-4843-8dd5-19316f7a4004'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'f57e54d2-2f04-4e96-b715-19aa7fdcd001'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'f57e54d2-2f04-4e96-b715-19aa7fdcd002'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'f57e54d2-2f04-4e96-b715-19aa7fdcd003'),
  ('0785700a-ec07-476f-b610-4488e2279be0', 'f57e54d2-2f04-4e96-b715-19aa7fdcd004')
ON DUPLICATE KEY UPDATE menu_id = VALUES(menu_id);

INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, remark, create_time, update_time)
VALUES
  ('1f0d4bb4-3f9a-47ce-a8d7-6717ac8fe001', '用户性别', 'sys_user_sex', 1, '用户性别列表', NOW(), NOW()),
  ('1f0d4bb4-3f9a-47ce-a8d7-6717ac8fe002', '系统状态', 'sys_common_status', 1, '系统状态列表', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  dict_name = VALUES(dict_name),
  status = VALUES(status),
  remark = VALUES(remark),
  update_time = VALUES(update_time);

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, remark, create_time, update_time)
VALUES
  ('2a1d9f34-c68d-4d5e-a4a8-c8f6f35a0001', 1, '男', '1', 'sys_user_sex', '', 'primary', 'N', 1, '性别男', NOW(), NOW()),
  ('2a1d9f34-c68d-4d5e-a4a8-c8f6f35a0002', 2, '女', '0', 'sys_user_sex', '', 'success', 'N', 1, '性别女', NOW(), NOW()),
  ('2a1d9f34-c68d-4d5e-a4a8-c8f6f35a0003', 0, '启用', '1', 'sys_common_status', '', 'success', 'Y', 1, '默认启用', NOW(), NOW()),
  ('2a1d9f34-c68d-4d5e-a4a8-c8f6f35a0004', 1, '停用', '0', 'sys_common_status', '', 'info', 'N', 1, '停用状态', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  dict_sort = VALUES(dict_sort),
  dict_label = VALUES(dict_label),
  css_class = VALUES(css_class),
  list_class = VALUES(list_class),
  is_default = VALUES(is_default),
  status = VALUES(status),
  remark = VALUES(remark),
  update_time = VALUES(update_time);
