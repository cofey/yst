package com.shunbo.yst.modules.system.dict.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictDataUpdateRequest {

    private Integer dictSort;

    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    private String dictValue;

    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    private String cssClass;
    private String listClass;
    private String isDefault;
    private Integer status;
    private String remark;
}
