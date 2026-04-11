package com.shunbo.yst.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shunbo.yst.modules.system.user.entity.SysUser;
import com.shunbo.yst.modules.system.user.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class AuthPermissionService {

    private final SysUserMapper sysUserMapper;

    public AuthPermissionService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public LoginUser loadLoginUser(String userId) {
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
        return loginUser;
    }

    public List<String> listRoleKeys(String userId) {
        return sysUserMapper.selectRoleKeysByUserId(userId);
    }

    public List<String> listPermissions(String userId) {
        return sysUserMapper.selectPermissionsByUserId(userId);
    }
}
