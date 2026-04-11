package com.shunbo.yst.modules.system.dict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dict_type")
public class SysDictType {

    @TableId(type = IdType.ASSIGN_UUID)
    private String dictId;

    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;
    @DateTimePattern
    private LocalDateTime createTime;
    @DateTimePattern
    private LocalDateTime updateTime;
}
