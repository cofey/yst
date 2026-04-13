package com.shunbo.yst.modules.system.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.entity.SysCompany;
import com.shunbo.yst.modules.system.service.CompanyService;
import com.shunbo.yst.modules.system.vo.CompanyQueryRequest;
import com.shunbo.yst.modules.system.vo.CompanySaveRequest;
import com.shunbo.yst.modules.system.vo.CompanyUpdateRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * CompanyController 控制器，负责处理对应模块的接口请求。
 */
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "CompanyController", description = "CompanyController 控制器")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 执行list相关处理。
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:company:list')")
    @Operation(summary = "list")
    public ApiResponse<PageResult<SysCompany>> list(@ModelAttribute CompanyQueryRequest query) {
        return ApiResponse.ok(companyService.list(query));
    }

    /**
     * 执行create相关处理。
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:company:add')")
    @Operation(summary = "create")
    public ApiResponse<Void> create(@RequestBody @Valid CompanySaveRequest request) {
        companyService.create(request);
        return ApiResponse.ok();
    }

    /**
     * 执行update相关处理。
     */
    @PutMapping("/{companyId}")
    @PreAuthorize("@ss.hasPermi('system:company:edit')")
    @Operation(summary = "update")
    public ApiResponse<Void> update(@PathVariable String companyId, @RequestBody @Valid CompanyUpdateRequest request) {
        companyService.update(companyId, request);
        return ApiResponse.ok();
    }

    /**
     * 执行delete相关处理。
     */
    @DeleteMapping("/{companyId}")
    @PreAuthorize("@ss.hasPermi('system:company:remove')")
    @Operation(summary = "delete")
    public ApiResponse<Void> delete(@PathVariable String companyId) {
        companyService.delete(companyId);
        return ApiResponse.ok();
    }

    /**
     * 执行importExcel相关处理。
     */
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('system:company:import')")
    @Operation(summary = "importExcel")
    public ApiResponse<Void> importExcel(@RequestParam("file") MultipartFile file) {
        companyService.importExcel(file);
        return ApiResponse.ok();
    }

    /**
     * 执行exportExcel相关处理。
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('system:company:export')")
    @Operation(summary = "exportExcel")
    public void exportExcel(HttpServletResponse response) {
        companyService.exportExcel(response);
    }
}
