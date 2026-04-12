package com.shunbo.yst.modules.system.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.entity.SysRole;
import com.shunbo.yst.modules.system.vo.RoleQueryRequest;
import com.shunbo.yst.modules.system.vo.RoleSaveRequest;
import com.shunbo.yst.modules.system.vo.RoleUpdateRequest;

import java.util.List;

/**
 * RoleService 服务接口，定义业务能力与契约。
 */
public interface RoleService {
    /**
     * 执行list相关处理。
     */
    PageResult<SysRole> list(RoleQueryRequest query);

    /**
     * 执行create相关处理。
     */

    void create(RoleSaveRequest request);

    /**
     * 执行update相关处理。
     */

    void update(String roleId, RoleUpdateRequest request);

    /**
     * 执行delete相关处理。
     */

    void delete(String roleId);

    /**
     * 执行menuIds相关处理。
     */

    List<String> menuIds(String roleId);

    /**
     * 执行assignMenus相关处理。
     */

    void assignMenus(String roleId, List<String> menuIds);
}
