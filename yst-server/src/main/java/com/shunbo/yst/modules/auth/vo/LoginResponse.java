package com.shunbo.yst.modules.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String token;
    private String username;
}
