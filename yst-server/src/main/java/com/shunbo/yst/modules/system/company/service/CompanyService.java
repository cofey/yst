package com.shunbo.yst.modules.system.company.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.company.entity.SysCompany;
import com.shunbo.yst.modules.system.company.vo.CompanyQueryRequest;
import com.shunbo.yst.modules.system.company.vo.CompanySaveRequest;
import com.shunbo.yst.modules.system.company.vo.CompanyUpdateRequest;

public interface CompanyService {
    PageResult<SysCompany> list(CompanyQueryRequest query);

    void create(CompanySaveRequest request);

    void update(String companyId, CompanyUpdateRequest request);

    void delete(String companyId);
}
