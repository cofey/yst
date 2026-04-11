package com.shunbo.yst.modules.system.company.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanySaveRequest {

    @NotBlank(message = "单位编码不能为空")
    private String companyCode;

    @NotBlank(message = "单位名称不能为空")
    private String companyName;

    private Integer status;
}
