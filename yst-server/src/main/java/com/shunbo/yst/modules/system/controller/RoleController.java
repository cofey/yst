package com.shunbo.yst.modules.system.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.entity.SysRole;
import com.shunbo.yst.modules.system.service.RoleService;
import com.shunbo.yst.modules.system.vo.RoleMenuAssignRequest;
import com.shunbo.yst.modules.system.vo.RoleQueryRequest;
import com.shunbo.yst.modules.system.vo.RoleSaveRequest;
import com.shunbo.yst.modules.system.vo.RoleUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * RoleController 控制器，负责处理对应模块的接口请求。
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "RoleController", description = "RoleController 控制器")
public class RoleController {

    private final RoleService roleService;

    /**
     * 执行list相关处理。
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @Operation(summary = "list")
    public ApiResponse<PageResult<SysRole>> list(@ModelAttribute RoleQueryRequest query) {
        return ApiResponse.ok(roleService.list(query));
    }

    /**
     * 执行create相关处理。
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Operation(summary = "create")
    public ApiResponse<Void> create(@RequestBody @Valid RoleSaveRequest request) {
        roleService.create(request);
        return ApiResponse.ok();
    }

    /**
     * 执行update相关处理。
     */
    @PutMapping("/{roleId}")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Operation(summary = "update")
    public ApiResponse<Void> update(@PathVariable String roleId, @RequestBody @Valid RoleUpdateRequest request) {
        roleService.update(roleId, request);
        return ApiResponse.ok();
    }

    /**
     * 执行delete相关处理。
     */
    @DeleteMapping("/{roleId}")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Operation(summary = "delete")
    public ApiResponse<Void> delete(@PathVariable String roleId) {
        roleService.delete(roleId);
        return ApiResponse.ok();
    }

    /**
     * 执行menuIds相关处理。
     */
    @GetMapping("/{roleId}/menus")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Operation(summary = "menuIds")
    public ApiResponse<List<String>> menuIds(@PathVariable String roleId) {
        return ApiResponse.ok(roleService.menuIds(roleId));
    }

    /**
     * 执行assignMenus相关处理。
     */
    @PutMapping("/{roleId}/menus")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Operation(summary = "assignMenus")
    public ApiResponse<Void> assignMenus(@PathVariable String roleId, @RequestBody RoleMenuAssignRequest request) {
        roleService.assignMenus(roleId, request.getMenuIds());
        return ApiResponse.ok();
    }
}
