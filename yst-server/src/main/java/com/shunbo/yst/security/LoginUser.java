package com.shunbo.yst.security;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class LoginUser {
    private String userId;
    private String username;
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();
}
