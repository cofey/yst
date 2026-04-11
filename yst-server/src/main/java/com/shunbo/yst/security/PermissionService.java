package com.shunbo.yst.security;

import org.springframework.stereotype.Component;

@Component("ss")
public class PermissionService {

    public boolean hasPermi(String permission) {
        LoginUser loginUser = CurrentUserUtils.getCurrentUserOrNull();
        if (loginUser == null) {
            return false;
        }
        return isAdmin(loginUser) || loginUser.getPermissions().contains(permission);
    }

    public boolean hasRole(String roleKey) {
        LoginUser loginUser = CurrentUserUtils.getCurrentUserOrNull();
        if (loginUser == null) {
            return false;
        }
        return isAdmin(loginUser) || loginUser.getRoles().contains(roleKey);
    }

    private boolean isAdmin(LoginUser loginUser) {
        return loginUser.getRoles().contains("admin");
    }
}
