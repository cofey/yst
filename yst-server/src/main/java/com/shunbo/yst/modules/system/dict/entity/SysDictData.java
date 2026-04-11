package com.shunbo.yst.modules.system.dict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dict_data")
public class SysDictData {

    @TableId(type = IdType.ASSIGN_UUID)
    private String dictCode;

    private Integer dictSort;
    private String dictLabel;
    private String dictValue;
    private String dictType;
    private String cssClass;
    private String listClass;
    private String isDefault;
    private Integer status;
    private String remark;
    @DateTimePattern
    private LocalDateTime createTime;
    @DateTimePattern
    private LocalDateTime updateTime;
}
