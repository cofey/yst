package com.shunbo.yst.modules.auth.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * AuthInfoVO 视图对象，用于接口数据传输。
 */
@Getter
@Setter
public class AuthInfoVO {
    private String userId;
    private String username;
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    /**
     * 执行getRoles相关处理。
     */

    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }

    /**
     * 执行setRoles相关处理。
     */

    public void setRoles(Set<String> roles) {
        this.roles = copySet(roles);
    }

    /**
     * 执行getPermissions相关处理。
     */

    public Set<String> getPermissions() {
        return new HashSet<>(permissions);
    }

    /**
     * 执行setPermissions相关处理。
     */

    public void setPermissions(Set<String> permissions) {
        this.permissions = copySet(permissions);
    }

    private static Set<String> copySet(Set<String> source) {
        return source == null ? new HashSet<>() : new HashSet<>(source);
    }
}
