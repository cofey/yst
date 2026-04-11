package com.shunbo.yst.modules.system.company.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.company.entity.SysCompany;
import com.shunbo.yst.modules.system.company.service.CompanyService;
import com.shunbo.yst.modules.system.company.vo.CompanyQueryRequest;
import com.shunbo.yst.modules.system.company.vo.CompanySaveRequest;
import com.shunbo.yst.modules.system.company.vo.CompanyUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:company:list')")
    public ApiResponse<PageResult<SysCompany>> list(@ModelAttribute CompanyQueryRequest query) {
        return ApiResponse.ok(companyService.list(query));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:company:add')")
    public ApiResponse<Void> create(@RequestBody @Valid CompanySaveRequest request) {
        companyService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping("/{companyId}")
    @PreAuthorize("@ss.hasPermi('system:company:edit')")
    public ApiResponse<Void> update(@PathVariable String companyId, @RequestBody @Valid CompanyUpdateRequest request) {
        companyService.update(companyId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{companyId}")
    @PreAuthorize("@ss.hasPermi('system:company:remove')")
    public ApiResponse<Void> delete(@PathVariable String companyId) {
        companyService.delete(companyId);
        return ApiResponse.ok();
    }
}
