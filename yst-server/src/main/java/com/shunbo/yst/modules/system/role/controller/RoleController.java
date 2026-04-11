package com.shunbo.yst.modules.system.role.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.role.entity.SysRole;
import com.shunbo.yst.modules.system.role.service.RoleService;
import com.shunbo.yst.modules.system.role.vo.RoleMenuAssignRequest;
import com.shunbo.yst.modules.system.role.vo.RoleQueryRequest;
import com.shunbo.yst.modules.system.role.vo.RoleSaveRequest;
import com.shunbo.yst.modules.system.role.vo.RoleUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public ApiResponse<PageResult<SysRole>> list(@ModelAttribute RoleQueryRequest query) {
        return ApiResponse.ok(roleService.list(query));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    public ApiResponse<Void> create(@RequestBody @Valid RoleSaveRequest request) {
        roleService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public ApiResponse<Void> update(@PathVariable String roleId, @RequestBody @Valid RoleUpdateRequest request) {
        roleService.update(roleId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    public ApiResponse<Void> delete(@PathVariable String roleId) {
        roleService.delete(roleId);
        return ApiResponse.ok();
    }

    @GetMapping("/{roleId}/menus")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public ApiResponse<List<String>> menuIds(@PathVariable String roleId) {
        return ApiResponse.ok(roleService.menuIds(roleId));
    }

    @PutMapping("/{roleId}/menus")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public ApiResponse<Void> assignMenus(@PathVariable String roleId, @RequestBody RoleMenuAssignRequest request) {
        roleService.assignMenus(roleId, request.getMenuIds());
        return ApiResponse.ok();
    }
}
