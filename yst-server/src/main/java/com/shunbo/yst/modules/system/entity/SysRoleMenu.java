package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * SysRoleMenu 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    // 角色ID
    private String roleId;
    // 菜单ID
    private String menuId;
}
