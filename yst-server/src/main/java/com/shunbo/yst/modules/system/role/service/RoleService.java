package com.shunbo.yst.modules.system.role.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.role.entity.SysRole;
import com.shunbo.yst.modules.system.role.vo.RoleQueryRequest;
import com.shunbo.yst.modules.system.role.vo.RoleSaveRequest;
import com.shunbo.yst.modules.system.role.vo.RoleUpdateRequest;

import java.util.List;

public interface RoleService {
    PageResult<SysRole> list(RoleQueryRequest query);

    void create(RoleSaveRequest request);

    void update(String roleId, RoleUpdateRequest request);

    void delete(String roleId);

    List<String> menuIds(String roleId);

    void assignMenus(String roleId, List<String> menuIds);
}
