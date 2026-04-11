package com.shunbo.yst.modules.system.menu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.ASSIGN_UUID)
    private String menuId;

    private String parentId;
    private String menuName;
    private String menuType;
    private String path;
    private String component;
    private String icon;
    private String perms;
    private Integer visible;
    private Integer status;
    private Integer sort;
    @DateTimePattern
    private LocalDateTime createTime;
    @DateTimePattern
    private LocalDateTime updateTime;
}
