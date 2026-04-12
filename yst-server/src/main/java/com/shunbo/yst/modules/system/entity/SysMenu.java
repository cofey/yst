package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SysMenu 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.ASSIGN_UUID)
    // 菜单ID
    private String menuId;

    // 父菜单ID
    private String parentId;
    // 菜单名称
    private String menuName;
    // 菜单类型
    private String menuType;
    // 路由路径
    private String path;
    // 组件路径
    private String component;
    // 菜单图标
    private String icon;
    // 权限标识
    private String perms;
    // 显示状态
    private Integer visible;
    // 状态
    private Integer status;
    // 排序值
    private Integer sort;
    @DateTimePattern
    // 创建时间
    private LocalDateTime createTime;
    @DateTimePattern
    // 更新时间
    private LocalDateTime updateTime;
}
