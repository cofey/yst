package com.shunbo.yst.modules.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginResponse 响应对象，承载接口出参。
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String token;
    private String username;
}
