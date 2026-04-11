package com.shunbo.yst.modules.auth.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.modules.system.user.service.UserService;
import com.shunbo.yst.modules.auth.vo.LoginRequest;
import com.shunbo.yst.modules.auth.vo.LoginResponse;
import com.shunbo.yst.modules.auth.vo.AuthInfoVO;
import com.shunbo.yst.modules.system.menu.service.MenuService;
import com.shunbo.yst.modules.system.menu.vo.MenuTreeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final MenuService menuService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(userService.login(request));
    }

    @GetMapping("/info")
    public ApiResponse<AuthInfoVO> info() {
        return ApiResponse.ok(userService.currentAuthInfo());
    }

    @GetMapping("/menus")
    public ApiResponse<List<MenuTreeVO>> menus() {
        return ApiResponse.ok(menuService.listCurrentUserMenus());
    }
}
