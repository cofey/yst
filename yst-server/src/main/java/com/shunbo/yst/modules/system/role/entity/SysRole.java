package com.shunbo.yst.modules.system.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.ASSIGN_UUID)
    private String roleId;

    private String roleName;
    private String roleKey;
    private Integer status;
    @DateTimePattern
    private LocalDateTime createTime;
    @DateTimePattern
    private LocalDateTime updateTime;
}
