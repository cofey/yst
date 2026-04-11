package com.shunbo.yst.modules.system.company.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_company")
public class SysCompany {

    @TableId(type = IdType.ASSIGN_UUID)
    private String companyId;

    private String companyCode;
    private String companyName;
    private Integer status;
    @DateTimePattern
    private LocalDateTime createTime;
    @DateTimePattern
    private LocalDateTime updateTime;
}
