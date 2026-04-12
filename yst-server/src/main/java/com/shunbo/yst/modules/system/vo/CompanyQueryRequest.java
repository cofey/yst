package com.shunbo.yst.modules.system.vo;

import com.shunbo.yst.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CompanyQueryRequest 数据对象，用于承载业务数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "CompanyQueryRequest 对象")
public class CompanyQueryRequest extends PageQuery {

    @Schema(description = "公司编码")
    private String companyCode;
    @Schema(description = "公司名称")
    private String companyName;
    @Schema(description = "状态")
    private Integer status;
}
