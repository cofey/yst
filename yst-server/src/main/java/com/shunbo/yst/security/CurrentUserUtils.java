package com.shunbo.yst.security;

import com.shunbo.yst.common.BizException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUserUtils {

    private CurrentUserUtils() {
    }

    public static LoginUser getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return null;
        }
        return loginUser;
    }

    public static LoginUser getCurrentUser() {
        LoginUser loginUser = getCurrentUserOrNull();
        if (loginUser == null) {
            throw new BizException("未登录");
        }
        return loginUser;
    }
}
