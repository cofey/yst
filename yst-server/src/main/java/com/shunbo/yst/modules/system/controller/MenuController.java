package com.shunbo.yst.modules.system.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.modules.system.entity.SysMenu;
import com.shunbo.yst.modules.system.service.MenuService;
import com.shunbo.yst.modules.system.vo.MenuSaveRequest;
import com.shunbo.yst.modules.system.vo.MenuUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * MenuController 控制器，负责处理对应模块的接口请求。
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "MenuController", description = "MenuController 控制器")
public class MenuController {

    private final MenuService menuService;

    /**
     * 执行list相关处理。
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @Operation(summary = "list")
    public ApiResponse<List<SysMenu>> list() {
        return ApiResponse.ok(menuService.list());
    }

    /**
     * 执行create相关处理。
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Operation(summary = "create")
    public ApiResponse<Void> create(@RequestBody @Valid MenuSaveRequest request) {
        menuService.create(request);
        return ApiResponse.ok();
    }

    /**
     * 执行update相关处理。
     */
    @PutMapping("/{menuId}")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Operation(summary = "update")
    public ApiResponse<Void> update(@PathVariable String menuId, @RequestBody @Valid MenuUpdateRequest request) {
        menuService.update(menuId, request);
        return ApiResponse.ok();
    }

    /**
     * 执行delete相关处理。
     */
    @DeleteMapping("/{menuId}")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Operation(summary = "delete")
    public ApiResponse<Void> delete(@PathVariable String menuId) {
        menuService.delete(menuId);
        return ApiResponse.ok();
    }

    /**
     * 执行clearAllCache相关处理。
     */
    @DeleteMapping("/cache/all")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Operation(summary = "clearAllCache")
    public ApiResponse<Void> clearAllCache() {
        menuService.clearAllCache();
        return ApiResponse.ok();
    }
}
