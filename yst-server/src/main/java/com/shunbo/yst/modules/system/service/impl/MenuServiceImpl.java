package com.shunbo.yst.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shunbo.yst.common.BizException;

import com.shunbo.yst.modules.system.entity.SysMenu;
import com.shunbo.yst.modules.system.mapper.SysMenuMapper;
import com.shunbo.yst.modules.system.mapper.SysRoleMenuMapper;
import com.shunbo.yst.modules.system.service.MenuService;
import com.shunbo.yst.modules.system.vo.MenuSaveRequest;
import com.shunbo.yst.modules.system.vo.MenuTreeVO;
import com.shunbo.yst.modules.system.vo.MenuUpdateRequest;
import com.shunbo.yst.security.AuthPermissionService;
import com.shunbo.yst.security.CurrentUserUtils;
import com.shunbo.yst.security.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * MenuServiceImpl 服务实现，负责执行业务流程与数据编排。
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final AuthPermissionService authPermissionService;

    /**
      * 执行list相关处理。
      */
    @Override
    public List<SysMenu> list() {
        return sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getMenuId));
    }

    /**
      * 执行listCurrentUserMenus相关处理。
      */
    @Override
    public List<MenuTreeVO> listCurrentUserMenus() {
        LoginUser loginUser = CurrentUserUtils.getCurrentUser();
        List<SysMenu> menus = loginUser.getRoles().contains("admin")
                ? sysMenuMapper.selectEnabledNavMenus()
                : sysMenuMapper.selectEnabledNavMenusByUserId(loginUser.getUserId());
        return toTree(menus);
    }

    /**
      * 执行create相关处理。
      */
    @Override
    public void create(MenuSaveRequest request) {
        validateMenuType(request.getMenuType());
        checkPermUnique(request.getPerms(), null);
        SysMenu menu = new SysMenu();
        menu.setMenuId(UUID.randomUUID().toString());
        fillMenu(menu, request.getParentId(), request.getMenuName(), request.getMenuType(), request.getPath(),
                request.getComponent(), request.getIcon(), request.getPerms(), request.getVisible(), request.getStatus(),
                request.getSort());
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        sysMenuMapper.insert(menu);
        authPermissionService.evictAllLoginUsers();
    }

    /**
      * 执行update相关处理。
      */
    @Override
    public void update(String menuId, MenuUpdateRequest request) {
        SysMenu menu = sysMenuMapper.selectById(menuId);
        if (menu == null) {
            throw new BizException("菜单不存在");
        }
        validateMenuType(request.getMenuType());
        checkPermUnique(request.getPerms(), menuId);
        fillMenu(menu, request.getParentId(), request.getMenuName(), request.getMenuType(), request.getPath(),
                request.getComponent(), request.getIcon(), request.getPerms(), request.getVisible(), request.getStatus(),
                request.getSort());
        menu.setUpdateTime(LocalDateTime.now());
        sysMenuMapper.updateById(menu);
        authPermissionService.evictAllLoginUsers();
    }

    /**
      * 执行delete相关处理。
      */
    @Override
    public void delete(String menuId) {
        SysMenu menu = sysMenuMapper.selectById(menuId);
        if (menu == null) {
            throw new BizException("菜单不存在");
        }
        long children = sysMenuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
        if (children > 0) {
            throw new BizException("请先删除子菜单");
        }
        long roleRef = sysRoleMenuMapper.selectCount(new LambdaQueryWrapper<com.shunbo.yst.modules.system.entity.SysRoleMenu>()
                .eq(com.shunbo.yst.modules.system.entity.SysRoleMenu::getMenuId, menuId));
        if (roleRef > 0) {
            throw new BizException("菜单已被角色引用，无法删除");
        }
        sysMenuMapper.deleteById(menuId);
        authPermissionService.evictAllLoginUsers();
    }

    /**
      * 执行clearAllCache相关处理。
      */
    @Override
    public void clearAllCache() {
        authPermissionService.evictAllLoginUsers();
    }

    private void validateMenuType(String menuType) {
        if (!"M".equals(menuType) && !"C".equals(menuType) && !"F".equals(menuType)) {
            throw new BizException("菜单类型必须是 M/C/F");
        }
    }

    private void checkPermUnique(String perms, String excludeId) {
        if (!StringUtils.hasText(perms)) {
            return;
        }
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getPerms, perms);
        if (excludeId != null) {
            wrapper.ne(SysMenu::getMenuId, excludeId);
        }
        SysMenu exists = sysMenuMapper.selectOne(wrapper.last("limit 1"));
        if (exists != null) {
            throw new BizException("权限标识已存在");
        }
    }

    private void fillMenu(SysMenu menu, String parentId, String menuName, String menuType, String path,
                          String component, String icon, String perms, Integer visible, Integer status, Integer sort) {
        menu.setParentId(parentId);
        menu.setMenuName(menuName);
        menu.setMenuType(menuType);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setIcon(icon);
        menu.setPerms(perms);
        menu.setVisible(Objects.requireNonNullElse(visible, 1));
        menu.setStatus(Objects.requireNonNullElse(status, 1));
        menu.setSort(Objects.requireNonNullElse(sort, 0));
    }

    private List<MenuTreeVO> toTree(List<SysMenu> menus) {
        Map<String, MenuTreeVO> nodeMap = new LinkedHashMap<>();
        for (SysMenu menu : menus) {
            MenuTreeVO node = new MenuTreeVO();
            BeanUtils.copyProperties(menu, node);
            node.setChildren(new ArrayList<>());
            nodeMap.put(menu.getMenuId(), node);
        }
        List<MenuTreeVO> roots = new ArrayList<>();
        for (MenuTreeVO node : nodeMap.values()) {
            String parentId = node.getParentId();
            if (StringUtils.hasText(parentId) && nodeMap.containsKey(parentId)) {
                MenuTreeVO parentNode = nodeMap.get(parentId);
                List<MenuTreeVO> parentChildren = parentNode.getChildren();
                parentChildren.add(node);
                parentNode.setChildren(parentChildren);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }
}
