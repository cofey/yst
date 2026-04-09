package com.yst.controller;

import com.yst.common.ApiResponse;
import com.yst.service.UserService;
import com.yst.vo.LoginRequest;
import com.yst.vo.LoginResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring manages bean lifecycle for injected dependencies")
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.ok(userService.login(request));
    }
}
