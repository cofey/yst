package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SysCompany 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_company")
public class SysCompany {

    @TableId(type = IdType.ASSIGN_UUID)
    // 公司ID
    private String companyId;

    // 公司编码
    private String companyCode;
    // 公司名称
    private String companyName;
    // 状态
    private Integer status;
    @DateTimePattern
    // 创建时间
    private LocalDateTime createTime;
    @DateTimePattern
    // 更新时间
    private LocalDateTime updateTime;
}
