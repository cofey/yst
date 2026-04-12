package com.shunbo.yst.modules.system.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.service.UserService;
import com.shunbo.yst.modules.system.vo.UserQueryRequest;
import com.shunbo.yst.modules.system.vo.UserSaveRequest;
import com.shunbo.yst.modules.system.vo.UserUpdateRequest;
import com.shunbo.yst.modules.system.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * UserController 控制器，负责处理对应模块的接口请求。
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "UserController 控制器")
public class UserController {

    private final UserService userService;

    /**
     * 执行list相关处理。
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @Operation(summary = "list")
    public ApiResponse<PageResult<UserVO>> list(@ModelAttribute UserQueryRequest query) {
        return ApiResponse.ok(userService.list(query));
    }

    /**
     * 执行create相关处理。
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Operation(summary = "create")
    public ApiResponse<Void> create(@RequestBody @Valid UserSaveRequest request) {
        userService.create(request);
        return ApiResponse.ok();
    }

    /**
     * 执行update相关处理。
     */
    @PutMapping("/{userId}")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Operation(summary = "update")
    public ApiResponse<Void> update(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        userService.update(userId, request);
        return ApiResponse.ok();
    }

    /**
     * 执行delete相关处理。
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Operation(summary = "delete")
    public ApiResponse<Void> delete(@PathVariable String userId) {
        userService.delete(userId);
        return ApiResponse.ok();
    }

    /**
     * 执行importExcel相关处理。
     */
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @Operation(summary = "importExcel")
    public ApiResponse<Void> importExcel(@RequestParam("file") MultipartFile file) {
        userService.importExcel(file);
        return ApiResponse.ok();
    }

    /**
     * 执行exportExcel相关处理。
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @Operation(summary = "exportExcel")
    public void exportExcel(HttpServletResponse response) {
        userService.exportExcel(response);
    }
}
