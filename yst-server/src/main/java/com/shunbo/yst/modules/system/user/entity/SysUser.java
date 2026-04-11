package com.shunbo.yst.modules.system.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    @DateTimePattern
    private LocalDateTime createTime;
    @DateTimePattern
    private LocalDateTime updateTime;
}
