package com.shunbo.yst.modules.system.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * CompanyExcelRow 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "CompanyExcelRow 对象")
public class CompanyExcelRow {

    @ExcelProperty("单位编码")
    @Schema(description = "公司编码")
    private String companyCode;

    @ExcelProperty("单位名称")
    @Schema(description = "公司名称")
    private String companyName;

    @ExcelProperty("状态(1启用/0禁用)")
    @Schema(description = "状态")
    private Integer status;
}
