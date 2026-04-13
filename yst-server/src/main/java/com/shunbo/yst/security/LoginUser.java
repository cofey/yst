package com.shunbo.yst.security;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class LoginUser {
    private String userId;
    private String username;
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }

    public void setRoles(Set<String> roles) {
        this.roles = copySet(roles);
    }

    public Set<String> getPermissions() {
        return new HashSet<>(permissions);
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = copySet(permissions);
    }

    private static Set<String> copySet(Set<String> source) {
        return source == null ? new HashSet<>() : new HashSet<>(source);
    }
}
