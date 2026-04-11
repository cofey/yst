package com.shunbo.yst.modules.system.company.vo;

import com.shunbo.yst.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyQueryRequest extends PageQuery {

    private String companyCode;
    private String companyName;
    private Integer status;
}
