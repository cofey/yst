package com.shunbo.yst.modules.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.menu.entity.SysMenu;
import com.shunbo.yst.modules.system.role.entity.SysRole;
import com.shunbo.yst.modules.system.role.entity.SysRoleMenu;
import com.shunbo.yst.modules.system.user.entity.SysUserRole;
import com.shunbo.yst.modules.system.menu.mapper.SysMenuMapper;
import com.shunbo.yst.modules.system.role.mapper.SysRoleMapper;
import com.shunbo.yst.modules.system.role.mapper.SysRoleMenuMapper;
import com.shunbo.yst.modules.system.user.mapper.SysUserRoleMapper;
import com.shunbo.yst.modules.system.role.service.RoleService;
import com.shunbo.yst.modules.system.role.vo.RoleQueryRequest;
import com.shunbo.yst.modules.system.role.vo.RoleSaveRequest;
import com.shunbo.yst.modules.system.role.vo.RoleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    public PageResult<SysRole> list(RoleQueryRequest query) {
        long pageNum = query.validPageNum();
        long pageSize = query.validPageSize();
        String roleName = query.getRoleName();
        String roleKey = query.getRoleKey();
        Integer status = query.getStatus();
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .like(StringUtils.hasText(roleKey), SysRole::getRoleKey, roleKey)
                .eq(status != null, SysRole::getStatus, status);
        applyListSort(wrapper, query);
        Page<SysRole> page = sysRoleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page);
    }

    private void applyListSort(LambdaQueryWrapper<SysRole> wrapper, RoleQueryRequest query) {
        boolean asc = query.normalizedSortAsc();
        String field = query.normalizedSortField();
        switch (field) {
            case "roleName":
            case "role_name":
                wrapper.orderBy(true, asc, SysRole::getRoleName);
                break;
            case "roleKey":
            case "role_key":
                wrapper.orderBy(true, asc, SysRole::getRoleKey);
                break;
            case "status":
                wrapper.orderBy(true, asc, SysRole::getStatus);
                break;
            case "createTime":
            case "create_time":
                wrapper.orderBy(true, asc, SysRole::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(SysRole::getCreateTime);
                break;
        }
    }

    @Override
    public void create(RoleSaveRequest request) {
        checkRoleKeyUnique(request.getRoleKey(), null);
        SysRole role = new SysRole();
        role.setRoleId(UUID.randomUUID().toString());
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);
        assignMenus(role.getRoleId(), request.getMenuIds());
    }

    @Override
    public void update(String roleId, RoleUpdateRequest request) {
        SysRole role = getEnabledRole(roleId);
        checkRoleKeyUnique(request.getRoleKey(), roleId);
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.updateById(role);
    }

    @Override
    public void delete(String roleId) {
        SysRole role = getEnabledRole(roleId);
        if ("admin".equals(role.getRoleKey())) {
            throw new BizException("超级管理员角色不允许删除");
        }
        sysRoleMenuMapper.deleteByRoleId(roleId);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
        sysRoleMapper.deleteById(roleId);
    }

    @Override
    public List<String> menuIds(String roleId) {
        getEnabledRole(roleId);
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public void assignMenus(String roleId, List<String> menuIds) {
        getEnabledRole(roleId);
        Set<String> normalizedIds = normalizeMenuIds(menuIds);
        sysRoleMenuMapper.deleteByRoleId(roleId);
        if (normalizedIds.isEmpty()) {
            return;
        }
        for (String menuId : normalizedIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(roleMenu);
        }
    }

    private Set<String> normalizeMenuIds(List<String> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<SysMenu> allMenus = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .select(SysMenu::getMenuId, SysMenu::getParentId));
        Map<String, String> parentMap = new HashMap<>();
        for (SysMenu menu : allMenus) {
            parentMap.put(menu.getMenuId(), menu.getParentId());
        }

        Set<String> result = new LinkedHashSet<>();
        List<String> invalidIds = new ArrayList<>();
        for (String menuId : menuIds) {
            if (menuId == null) {
                continue;
            }
            if (!parentMap.containsKey(menuId)) {
                invalidIds.add(menuId);
                continue;
            }
            String current = menuId;
            while (current != null && parentMap.containsKey(current)) {
                if (!result.add(current)) {
                    break;
                }
                current = parentMap.get(current);
            }
        }

        if (!invalidIds.isEmpty()) {
            throw new BizException("包含不存在的菜单ID: " + invalidIds);
        }
        return result;
    }

    private void checkRoleKeyUnique(String roleKey, String excludeRoleId) {
        if (!StringUtils.hasText(roleKey)) {
            return;
        }
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, roleKey);
        if (excludeRoleId != null) {
            wrapper.ne(SysRole::getRoleId, excludeRoleId);
        }
        SysRole exists = sysRoleMapper.selectOne(wrapper.last("limit 1"));
        if (exists != null) {
            throw new BizException("角色标识已存在");
        }
    }

    private SysRole getEnabledRole(String roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BizException("角色不存在");
        }
        return role;
    }
}
