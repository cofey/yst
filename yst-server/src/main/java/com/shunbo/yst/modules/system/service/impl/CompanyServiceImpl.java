package com.shunbo.yst.modules.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.component.QuerySortComponent;

import com.shunbo.yst.modules.system.entity.SysCompany;
import com.shunbo.yst.modules.system.entity.SysUserCompany;
import com.shunbo.yst.modules.system.mapper.SysCompanyMapper;
import com.shunbo.yst.modules.system.mapper.SysUserCompanyMapper;
import com.shunbo.yst.modules.system.service.CompanyService;
import com.shunbo.yst.modules.system.vo.CompanyExcelRow;
import com.shunbo.yst.modules.system.vo.CompanyQueryRequest;
import com.shunbo.yst.modules.system.vo.CompanySaveRequest;
import com.shunbo.yst.modules.system.vo.CompanyUpdateRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

/**
 * CompanyServiceImpl 服务实现，负责执行业务流程与数据编排。
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private static final Map<String, SFunction<SysCompany, ?>> LIST_SORT_COLUMNS = buildSortColumns();

    private final SysCompanyMapper sysCompanyMapper;
    private final SysUserCompanyMapper sysUserCompanyMapper;
    private final QuerySortComponent querySortComponent;

    /**
     * 执行list相关处理。
     */
    @Override
    public PageResult<SysCompany> list(CompanyQueryRequest query) {
        long pageNum = query.validPageNum();
        long pageSize = query.validPageSize();
        String companyCode = query.getCompanyCode();
        String companyName = query.getCompanyName();
        Integer status = query.getStatus();
        LambdaQueryWrapper<SysCompany> wrapper = new LambdaQueryWrapper<SysCompany>()
                .like(StringUtils.hasText(companyCode), SysCompany::getCompanyCode, companyCode)
                .like(StringUtils.hasText(companyName), SysCompany::getCompanyName, companyName)
                .eq(status != null, SysCompany::getStatus, status);
        applyListSort(wrapper, query);
        Page<SysCompany> page = sysCompanyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page);
    }

    private void applyListSort(LambdaQueryWrapper<SysCompany> wrapper, CompanyQueryRequest query) {
        querySortComponent.applySort(wrapper, query.normalizedSortField(), query.normalizedSortAsc(),
                SysCompany::getCreateTime, LIST_SORT_COLUMNS);
    }

    private static Map<String, SFunction<SysCompany, ?>> buildSortColumns() {
        Map<String, SFunction<SysCompany, ?>> sortColumns = new HashMap<>();
        sortColumns.put("companyCode", SysCompany::getCompanyCode);
        sortColumns.put("company_code", SysCompany::getCompanyCode);
        sortColumns.put("companyName", SysCompany::getCompanyName);
        sortColumns.put("company_name", SysCompany::getCompanyName);
        sortColumns.put("status", SysCompany::getStatus);
        sortColumns.put("createTime", SysCompany::getCreateTime);
        sortColumns.put("create_time", SysCompany::getCreateTime);
        return sortColumns;
    }

    /**
     * 执行create相关处理。
     */
    @Override
    public void create(CompanySaveRequest request) {
        checkCompanyCodeUnique(request.getCompanyCode(), null);
        checkCompanyNameUnique(request.getCompanyName(), null);
        SysCompany company = new SysCompany();
        company.setCompanyId(UUID.randomUUID().toString());
        company.setCompanyCode(request.getCompanyCode());
        company.setCompanyName(request.getCompanyName());
        company.setStatus(Objects.requireNonNullElse(request.getStatus(), 1));
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        sysCompanyMapper.insert(company);
    }

    /**
     * 执行update相关处理。
     */
    @Override
    public void update(String companyId, CompanyUpdateRequest request) {
        SysCompany company = getEnabledCompany(companyId);
        checkCompanyCodeUnique(request.getCompanyCode(), companyId);
        checkCompanyNameUnique(request.getCompanyName(), companyId);
        company.setCompanyCode(request.getCompanyCode());
        company.setCompanyName(request.getCompanyName());
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }
        company.setUpdateTime(LocalDateTime.now());
        sysCompanyMapper.updateById(company);
    }

    /**
     * 执行delete相关处理。
     */
    @Override
    public void delete(String companyId) {
        getEnabledCompany(companyId);
        long usedCount = sysUserCompanyMapper.selectCount(new LambdaQueryWrapper<SysUserCompany>()
                .eq(SysUserCompany::getCompanyId, companyId));
        if (usedCount > 0) {
            throw new BizException("该单位已被用户关联，无法删除");
        }
        sysCompanyMapper.deleteById(companyId);
    }

    /**
     * 执行importExcel相关处理。
     */
    @Override
    public void importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("请上传文件");
        }
        List<CompanyExcelRow> rows = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), CompanyExcelRow.class,
                    new PageReadListener<CompanyExcelRow>(rows::addAll)).sheet().doRead();
        } catch (IOException e) {
            throw new BizException("读取Excel失败: " + e.getMessage());
        }

        for (CompanyExcelRow row : rows) {
            if (!StringUtils.hasText(row.getCompanyCode()) || !StringUtils.hasText(row.getCompanyName())) {
                continue;
            }
            SysCompany company = sysCompanyMapper.selectOne(new LambdaQueryWrapper<SysCompany>()
                    .eq(SysCompany::getCompanyCode, row.getCompanyCode())
                    .last("limit 1"));
            boolean exists = company != null;
            if (company == null) {
                checkCompanyCodeUnique(row.getCompanyCode(), null);
                checkCompanyNameUnique(row.getCompanyName(), null);
                company = new SysCompany();
                company.setCompanyId(UUID.randomUUID().toString());
                company.setCompanyCode(row.getCompanyCode());
                company.setCreateTime(LocalDateTime.now());
            } else {
                checkCompanyNameUnique(row.getCompanyName(), company.getCompanyId());
            }
            company.setCompanyName(row.getCompanyName());
            company.setStatus(Objects.requireNonNullElse(row.getStatus(), 1));
            company.setUpdateTime(LocalDateTime.now());
            if (!exists) {
                sysCompanyMapper.insert(company);
            } else {
                sysCompanyMapper.updateById(company);
            }
        }
    }

    /**
     * 执行exportExcel相关处理。
     */
    @Override
    public void exportExcel(HttpServletResponse response) {
        List<SysCompany> companies = sysCompanyMapper.selectList(new LambdaQueryWrapper<SysCompany>()
                .orderByDesc(SysCompany::getCreateTime));
        List<CompanyExcelRow> rows = new ArrayList<>();
        for (SysCompany company : companies) {
            CompanyExcelRow row = new CompanyExcelRow();
            row.setCompanyCode(company.getCompanyCode());
            row.setCompanyName(company.getCompanyName());
            row.setStatus(company.getStatus());
            rows.add(row);
        }
        try {
            String fileName = URLEncoder.encode("单位列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), CompanyExcelRow.class).sheet("单位").doWrite(rows);
        } catch (IOException e) {
            throw new BizException("导出失败: " + e.getMessage());
        }
    }

    private SysCompany getEnabledCompany(String companyId) {
        SysCompany company = sysCompanyMapper.selectById(companyId);
        if (company == null) {
            throw new BizException("单位不存在");
        }
        return company;
    }

    private void checkCompanyCodeUnique(String companyCode, String excludeId) {
        if (!StringUtils.hasText(companyCode)) {
            return;
        }
        LambdaQueryWrapper<SysCompany> wrapper = new LambdaQueryWrapper<SysCompany>()
                .eq(SysCompany::getCompanyCode, companyCode);
        if (excludeId != null) {
            wrapper.ne(SysCompany::getCompanyId, excludeId);
        }
        SysCompany exists = sysCompanyMapper.selectOne(wrapper.last("limit 1"));
        if (exists != null) {
            throw new BizException("单位编码已存在");
        }
    }

    private void checkCompanyNameUnique(String companyName, String excludeId) {
        if (!StringUtils.hasText(companyName)) {
            return;
        }
        LambdaQueryWrapper<SysCompany> wrapper = new LambdaQueryWrapper<SysCompany>()
                .eq(SysCompany::getCompanyName, companyName);
        if (excludeId != null) {
            wrapper.ne(SysCompany::getCompanyId, excludeId);
        }
        SysCompany exists = sysCompanyMapper.selectOne(wrapper.last("limit 1"));
        if (exists != null) {
            throw new BizException("单位名称已存在");
        }
    }
}
