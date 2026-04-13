package com.shunbo.yst.modules.auth.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest 请求对象，承载接口入参。
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
