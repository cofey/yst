package com.shunbo.yst.modules.system.user.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.user.service.UserService;
import com.shunbo.yst.modules.system.user.vo.UserQueryRequest;
import com.shunbo.yst.modules.system.user.vo.UserSaveRequest;
import com.shunbo.yst.modules.system.user.vo.UserUpdateRequest;
import com.shunbo.yst.modules.system.user.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public ApiResponse<PageResult<UserVO>> list(@ModelAttribute UserQueryRequest query) {
        return ApiResponse.ok(userService.list(query));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    public ApiResponse<Void> create(@RequestBody @Valid UserSaveRequest request) {
        userService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public ApiResponse<Void> update(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        userService.update(userId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    public ApiResponse<Void> delete(@PathVariable String userId) {
        userService.delete(userId);
        return ApiResponse.ok();
    }

    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    public ApiResponse<Void> importExcel(@RequestParam("file") MultipartFile file) {
        userService.importExcel(file);
        return ApiResponse.ok();
    }

    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    public void exportExcel(HttpServletResponse response) {
        userService.exportExcel(response);
    }
}
