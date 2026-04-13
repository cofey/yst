package com.shunbo.yst.modules.system.vo;

import com.shunbo.yst.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DictTypeQueryRequest 数据对象，用于承载业务数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "DictTypeQueryRequest 对象")
public class DictTypeQueryRequest extends PageQuery {

    @Schema(description = "字典名称")
    private String dictName;
    @Schema(description = "字典类型")
    private String dictType;
    @Schema(description = "状态")
    private Integer status;
}
