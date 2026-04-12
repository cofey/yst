package com.shunbo.yst.modules.system.vo;

import com.shunbo.yst.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DictDataQueryRequest 数据对象，用于承载业务数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "DictDataQueryRequest 对象")
public class DictDataQueryRequest extends PageQuery {

    @Schema(description = "字典类型")
    private String dictType;
    @Schema(description = "字典标签")
    private String dictLabel;
    @Schema(description = "状态")
    private Integer status;
}
