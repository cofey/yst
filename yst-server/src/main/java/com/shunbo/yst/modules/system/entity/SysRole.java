package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SysRole 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.ASSIGN_UUID)
    // 角色ID
    private String roleId;

    // 角色名称
    private String roleName;
    // 角色权限标识
    private String roleKey;
    // 状态
    private Integer status;
    @DateTimePattern
    // 创建时间
    private LocalDateTime createTime;
    @DateTimePattern
    // 更新时间
    private LocalDateTime updateTime;
}
