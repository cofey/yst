package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CompanyUpdateRequest 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "CompanyUpdateRequest 对象")
public class CompanyUpdateRequest {

    @NotBlank(message = "单位编码不能为空")
    @Schema(description = "公司编码")
    private String companyCode;

    @NotBlank(message = "单位名称不能为空")
    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "状态")
    private Integer status;
}
