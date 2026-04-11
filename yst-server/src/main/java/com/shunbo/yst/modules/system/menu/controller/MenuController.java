package com.shunbo.yst.modules.system.menu.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.modules.system.menu.entity.SysMenu;
import com.shunbo.yst.modules.system.menu.service.MenuService;
import com.shunbo.yst.modules.system.menu.vo.MenuSaveRequest;
import com.shunbo.yst.modules.system.menu.vo.MenuUpdateRequest;
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

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    public ApiResponse<List<SysMenu>> list() {
        return ApiResponse.ok(menuService.list());
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    public ApiResponse<Void> create(@RequestBody @Valid MenuSaveRequest request) {
        menuService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping("/{menuId}")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    public ApiResponse<Void> update(@PathVariable String menuId, @RequestBody @Valid MenuUpdateRequest request) {
        menuService.update(menuId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{menuId}")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    public ApiResponse<Void> delete(@PathVariable String menuId) {
        menuService.delete(menuId);
        return ApiResponse.ok();
    }
}
