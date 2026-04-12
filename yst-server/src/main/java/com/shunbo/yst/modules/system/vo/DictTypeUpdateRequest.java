package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DictTypeUpdateRequest 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "DictTypeUpdateRequest 对象")
public class DictTypeUpdateRequest {

    @NotBlank(message = "字典名称不能为空")
    @Schema(description = "字典名称")
    private String dictName;

    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
}
