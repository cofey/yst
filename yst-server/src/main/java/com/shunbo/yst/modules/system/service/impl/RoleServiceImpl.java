package com.shunbo.yst.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.component.QuerySortComponent;
import com.shunbo.yst.component.RelationRebuildComponent;

import com.shunbo.yst.modules.system.entity.SysMenu;
import com.shunbo.yst.modules.system.entity.SysRole;
import com.shunbo.yst.modules.system.entity.SysRoleMenu;
import com.shunbo.yst.modules.system.entity.SysUserRole;
import com.shunbo.yst.modules.system.mapper.SysMenuMapper;
import com.shunbo.yst.modules.system.mapper.SysRoleMapper;
import com.shunbo.yst.modules.system.mapper.SysRoleMenuMapper;
import com.shunbo.yst.modules.system.mapper.SysUserMapper;
import com.shunbo.yst.modules.system.mapper.SysUserRoleMapper;
import com.shunbo.yst.modules.system.service.RoleService;
import com.shunbo.yst.modules.system.vo.RoleQueryRequest;
import com.shunbo.yst.modules.system.vo.RoleSaveRequest;
import com.shunbo.yst.modules.system.vo.RoleUpdateRequest;
import com.shunbo.yst.security.AuthPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * RoleServiceImpl 服务实现，负责执行业务流程与数据编排。
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final Map<String, SFunction<SysRole, ?>> LIST_SORT_COLUMNS = buildSortColumns();

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysUserMapper sysUserMapper;
    private final AuthPermissionService authPermissionService;
    private final QuerySortComponent querySortComponent;
    private final RelationRebuildComponent relationRebuildComponent;

    /**
      * 执行list相关处理。
      */
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
        querySortComponent.applySort(wrapper, query.normalizedSortField(), query.normalizedSortAsc(),
                SysRole::getCreateTime, LIST_SORT_COLUMNS);
    }

    /**
      * 执行create相关处理。
      */
    @Override
    public void create(RoleSaveRequest request) {
        checkRoleKeyUnique(request.getRoleKey(), null);
        SysRole role = new SysRole();
        role.setRoleId(UUID.randomUUID().toString());
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setStatus(Objects.requireNonNullElse(request.getStatus(), 1));
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);
        assignMenus(role.getRoleId(), request.getMenuIds());
    }

    /**
      * 执行update相关处理。
      */
    @Override
    public void update(String roleId, RoleUpdateRequest request) {
        SysRole role = getEnabledRole(roleId);
        List<String> userIds = sysUserMapper.selectUserIdsByRoleId(roleId);
        checkRoleKeyUnique(request.getRoleKey(), roleId);
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        role.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.updateById(role);
        authPermissionService.evictLoginUsers(userIds);
    }

    /**
      * 执行delete相关处理。
      */
    @Override
    public void delete(String roleId) {
        SysRole role = getEnabledRole(roleId);
        List<String> userIds = sysUserMapper.selectUserIdsByRoleId(roleId);
        if ("admin".equals(role.getRoleKey())) {
            throw new BizException("超级管理员角色不允许删除");
        }
        sysRoleMenuMapper.deleteByRoleId(roleId);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
        sysRoleMapper.deleteById(roleId);
        authPermissionService.evictLoginUsers(userIds);
    }

    /**
      * 执行menuIds相关处理。
      */
    @Override
    public List<String> menuIds(String roleId) {
        getEnabledRole(roleId);
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
      * 执行assignMenus相关处理。
      */
    @Override
    public void assignMenus(String roleId, List<String> menuIds) {
        getEnabledRole(roleId);
        List<String> userIds = sysUserMapper.selectUserIdsByRoleId(roleId);
        Set<String> normalizedIds = normalizeMenuIds(menuIds);
        if (normalizedIds.isEmpty()) {
            sysRoleMenuMapper.deleteByRoleId(roleId);
            authPermissionService.evictLoginUsers(userIds);
            return;
        }
        relationRebuildComponent.rebuild(normalizedIds,
                () -> sysRoleMenuMapper.deleteByRoleId(roleId),
                menuId -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    sysRoleMenuMapper.insert(roleMenu);
                });
        authPermissionService.evictLoginUsers(userIds);
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

    private static Map<String, SFunction<SysRole, ?>> buildSortColumns() {
        Map<String, SFunction<SysRole, ?>> sortColumns = new HashMap<>();
        sortColumns.put("roleName", SysRole::getRoleName);
        sortColumns.put("role_name", SysRole::getRoleName);
        sortColumns.put("roleKey", SysRole::getRoleKey);
        sortColumns.put("role_key", SysRole::getRoleKey);
        sortColumns.put("status", SysRole::getStatus);
        sortColumns.put("createTime", SysRole::getCreateTime);
        sortColumns.put("create_time", SysRole::getCreateTime);
        return sortColumns;
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
