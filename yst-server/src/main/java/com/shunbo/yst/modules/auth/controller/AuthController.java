package com.shunbo.yst.modules.auth.controller;

import com.shunbo.yst.common.ApiResponse;

import com.shunbo.yst.modules.system.service.UserService;
import com.shunbo.yst.modules.auth.vo.LoginRequest;
import com.shunbo.yst.modules.auth.vo.LoginResponse;
import com.shunbo.yst.modules.auth.vo.AuthInfoVO;
import com.shunbo.yst.modules.system.service.MenuService;
import com.shunbo.yst.modules.system.vo.MenuTreeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AuthController 控制器，负责处理对应模块的接口请求。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final MenuService menuService;

    /**
     * 执行login相关处理。
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(userService.login(request));
    }

    /**
     * 执行info相关处理。
     */
    @GetMapping("/info")
    public ApiResponse<AuthInfoVO> info() {
        return ApiResponse.ok(userService.currentAuthInfo());
    }

    /**
     * 执行menus相关处理。
     */
    @GetMapping("/menus")
    public ApiResponse<List<MenuTreeVO>> menus() {
        return ApiResponse.ok(menuService.listCurrentUserMenus());
    }
}
