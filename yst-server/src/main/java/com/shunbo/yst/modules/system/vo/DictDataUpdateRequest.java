package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DictDataUpdateRequest 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "DictDataUpdateRequest 对象")
public class DictDataUpdateRequest {

    @Schema(description = "字典排序")
    private Integer dictSort;

    @NotBlank(message = "字典标签不能为空")
    @Schema(description = "字典标签")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    @Schema(description = "字典键值")
    private String dictValue;

    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "样式属性")
    private String cssClass;
    @Schema(description = "表格回显样式")
    private String listClass;
    @Schema(description = "是否默认")
    private String isDefault;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
}
