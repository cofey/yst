package com.shunbo.yst.modules.system.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_company")
public class SysUserCompany {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String companyId;
}
