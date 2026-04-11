package com.shunbo.yst.modules.auth.vo;

import lombok.Data;

import java.util.Set;

@Data
public class AuthInfoVO {
    private String userId;
    private String username;
    private Set<String> roles;
    private Set<String> permissions;
}
