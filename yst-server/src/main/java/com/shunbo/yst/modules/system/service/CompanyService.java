package com.shunbo.yst.modules.system.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.entity.SysCompany;
import com.shunbo.yst.modules.system.vo.CompanyQueryRequest;
import com.shunbo.yst.modules.system.vo.CompanySaveRequest;
import com.shunbo.yst.modules.system.vo.CompanyUpdateRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * CompanyService 服务接口，定义业务能力与契约。
 */
public interface CompanyService {
    /**
     * 执行list相关处理。
     */
    PageResult<SysCompany> list(CompanyQueryRequest query);

    /**
     * 执行create相关处理。
     */

    void create(CompanySaveRequest request);

    /**
     * 执行update相关处理。
     */

    void update(String companyId, CompanyUpdateRequest request);

    /**
     * 执行delete相关处理。
     */

    void delete(String companyId);

    /**
     * 执行importExcel相关处理。
     */

    void importExcel(MultipartFile file);

    /**
     * 执行exportExcel相关处理。
     */

    void exportExcel(HttpServletResponse response);
}
