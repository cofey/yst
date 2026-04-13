package com.shunbo.yst.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.shunbo.yst.modules.system.entity.SysUser;
import com.shunbo.yst.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthPermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthPermissionService.class);
    private static final String LOGIN_USER_CACHE_KEY_PREFIX = "auth:login_user:";
    private static final Duration LOGIN_USER_CACHE_TTL = Duration.ofMinutes(30);

    private final SysUserMapper sysUserMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public LoginUser loadLoginUser(String userId) {
        LoginUser cache = getCache(userId);
        if (cache != null) {
            return cache;
        }
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, userId)
                .eq(SysUser::getStatus, 1)
                .last("limit 1"));
        if (user == null) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRoles(new HashSet<>(sysUserMapper.selectRoleKeysByUserId(userId)));
        loginUser.setPermissions(new HashSet<>(sysUserMapper.selectPermissionsByUserId(userId)));
        putCache(userId, loginUser);
        return loginUser;
    }

    public void evictLoginUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            return;
        }
        try {
            redisTemplate.delete(buildLoginUserKey(userId));
        } catch (RuntimeException e) {
            LOGGER.warn("清理登录用户缓存失败, userId={}", userId, e);
        }
    }

    public void evictLoginUsers(Collection<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        Set<String> keys = new HashSet<>();
        for (String userId : userIds) {
            if (StringUtils.hasText(userId)) {
                keys.add(buildLoginUserKey(userId));
            }
        }
        if (keys.isEmpty()) {
            return;
        }
        try {
            redisTemplate.delete(keys);
        } catch (RuntimeException e) {
            LOGGER.warn("批量清理登录用户缓存失败", e);
        }
    }

    public void evictAllLoginUsers() {
        try {
            Set<String> keys = redisTemplate.keys(LOGIN_USER_CACHE_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                return;
            }
            redisTemplate.delete(keys);
        } catch (RuntimeException e) {
            LOGGER.warn("清理全部登录用户缓存失败", e);
        }
    }

    private LoginUser getCache(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        try {
            String raw = redisTemplate.opsForValue().get(buildLoginUserKey(userId));
            if (!StringUtils.hasText(raw)) {
                return null;
            }
            return objectMapper.readValue(raw, LoginUser.class);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("读取登录用户缓存失败, userId={}", userId, e);
            return null;
        }
    }

    private void putCache(String userId, LoginUser loginUser) {
        if (!StringUtils.hasText(userId) || loginUser == null) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(
                    buildLoginUserKey(userId),
                    objectMapper.writeValueAsString(loginUser),
                    LOGIN_USER_CACHE_TTL
            );
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("写入登录用户缓存失败, userId={}", userId, e);
        }
    }

    private String buildLoginUserKey(String userId) {
        return LOGIN_USER_CACHE_KEY_PREFIX + userId;
    }
}
