package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DictOptionVO 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "DictOptionVO 对象")
public class DictOptionVO {
    @Schema(description = "选项标签")
    private String label;
    @Schema(description = "选项值")
    private String value;
    @Schema(description = "样式属性")
    private String cssClass;
    @Schema(description = "表格回显样式")
    private String listClass;
    @Schema(description = "是否默认")
    private String isDefault;
}
