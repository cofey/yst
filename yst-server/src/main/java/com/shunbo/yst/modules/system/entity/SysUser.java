package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SysUser 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.ASSIGN_UUID)
    // 用户ID
    private String userId;

    // 用户名
    private String username;
    // 密码
    private String password;
    // 用户昵称
    private String nickname;
    // 邮箱
    private String email;
    // 手机号
    private String phone;
    // 状态
    private Integer status;
    @DateTimePattern
    // 创建时间
    private LocalDateTime createTime;
    @DateTimePattern
    // 更新时间
    private LocalDateTime updateTime;
}
