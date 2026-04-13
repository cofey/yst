package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * SysUserRole 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    // 用户ID
    private String userId;
    // 角色ID
    private String roleId;
}
