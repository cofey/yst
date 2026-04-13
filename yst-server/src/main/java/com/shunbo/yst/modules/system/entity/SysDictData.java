package com.shunbo.yst.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SysDictData 数据对象，用于承载业务数据。
 */
@Data
@TableName("sys_dict_data")
public class SysDictData {

    @TableId(type = IdType.ASSIGN_UUID)
    // 字典数据ID
    private String dictCode;

    // 字典排序
    private Integer dictSort;
    // 字典标签
    private String dictLabel;
    // 字典键值
    private String dictValue;
    // 字典类型
    private String dictType;
    // 样式属性
    private String cssClass;
    // 表格回显样式
    private String listClass;
    // 是否默认
    private String isDefault;
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
