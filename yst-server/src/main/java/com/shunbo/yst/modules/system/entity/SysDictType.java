package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SysDictType 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_dict_type")
public class SysDictType {

    @TableId(type = IdType.ASSIGN_UUID)
    // 字典ID
    private String dictId;

    // 字典名称
    private String dictName;
    // 字典类型
    private String dictType;
    // 状态
    private Integer status;
    // 备注
    private String remark;
    @DateTimePattern
    // 创建时间
    private LocalDateTime createTime;
    @DateTimePattern
    // 更新时间
    private LocalDateTime updateTime;
}
