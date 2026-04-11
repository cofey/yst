package com.shunbo.yst.modules.system.menu.service;

import com.shunbo.yst.modules.system.menu.entity.SysMenu;
import com.shunbo.yst.modules.system.menu.vo.MenuSaveRequest;
import com.shunbo.yst.modules.system.menu.vo.MenuTreeVO;
import com.shunbo.yst.modules.system.menu.vo.MenuUpdateRequest;

import java.util.List;

public interface MenuService {
    List<SysMenu> list();

    List<MenuTreeVO> listCurrentUserMenus();

    void create(MenuSaveRequest request);

    void update(String menuId, MenuUpdateRequest request);

    void delete(String menuId);
}
