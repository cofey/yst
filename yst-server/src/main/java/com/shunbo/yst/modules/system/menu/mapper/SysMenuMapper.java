package com.shunbo.yst.modules.system.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shunbo.yst.modules.system.menu.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("""
            SELECT m.*
            FROM sys_menu m
            WHERE m.status = 1
              AND m.visible = 1
              AND m.menu_type IN ('M', 'C')
            ORDER BY m.parent_id, m.sort, m.menu_id
            """)
    List<SysMenu> selectEnabledNavMenus();

    @Select("""
            SELECT DISTINCT m.*
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.role_id
            JOIN sys_role_menu rm ON ur.role_id = rm.role_id
            JOIN sys_menu m ON rm.menu_id = m.menu_id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND m.status = 1
              AND m.visible = 1
              AND m.menu_type IN ('M', 'C')
            ORDER BY m.parent_id, m.sort, m.menu_id
            """)
    List<SysMenu> selectEnabledNavMenusByUserId(@Param("userId") String userId);
}
