package com.shunbo.yst.modules.system.service;

import com.shunbo.yst.modules.system.entity.SysMenu;
import com.shunbo.yst.modules.system.vo.MenuSaveRequest;
import com.shunbo.yst.modules.system.vo.MenuTreeVO;
import com.shunbo.yst.modules.system.vo.MenuUpdateRequest;

import java.util.List;

/**
 * MenuService 服务接口，定义业务能力与契约。
 */
public interface MenuService {
    /**
     * 执行list相关处理。
     */
    List<SysMenu> list();

    /**
     * 执行listCurrentUserMenus相关处理。
     */

    List<MenuTreeVO> listCurrentUserMenus();

    /**
     * 执行create相关处理。
     */

    void create(MenuSaveRequest request);

    /**
     * 执行update相关处理。
     */

    void update(String menuId, MenuUpdateRequest request);

    /**
     * 执行delete相关处理。
     */

    void delete(String menuId);

    /**
     * 执行clearAllCache相关处理。
     */

    void clearAllCache();
}
