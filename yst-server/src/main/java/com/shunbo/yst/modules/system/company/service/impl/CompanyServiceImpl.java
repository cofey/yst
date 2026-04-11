package com.shunbo.yst.modules.system.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.company.entity.SysCompany;
import com.shunbo.yst.modules.system.user.entity.SysUserCompany;
import com.shunbo.yst.modules.system.company.mapper.SysCompanyMapper;
import com.shunbo.yst.modules.system.user.mapper.SysUserCompanyMapper;
import com.shunbo.yst.modules.system.company.service.CompanyService;
import com.shunbo.yst.modules.system.company.vo.CompanyQueryRequest;
import com.shunbo.yst.modules.system.company.vo.CompanySaveRequest;
import com.shunbo.yst.modules.system.company.vo.CompanyUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final SysCompanyMapper sysCompanyMapper;
    private final SysUserCompanyMapper sysUserCompanyMapper;

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
        boolean asc = query.normalizedSortAsc();
        String field = query.normalizedSortField();
        switch (field) {
            case "companyCode":
            case "company_code":
                wrapper.orderBy(true, asc, SysCompany::getCompanyCode);
                break;
            case "companyName":
            case "company_name":
                wrapper.orderBy(true, asc, SysCompany::getCompanyName);
                break;
            case "status":
                wrapper.orderBy(true, asc, SysCompany::getStatus);
                break;
            case "createTime":
            case "create_time":
                wrapper.orderBy(true, asc, SysCompany::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(SysCompany::getCreateTime);
                break;
        }
    }

    @Override
    public void create(CompanySaveRequest request) {
        checkCompanyCodeUnique(request.getCompanyCode(), null);
        checkCompanyNameUnique(request.getCompanyName(), null);
        SysCompany company = new SysCompany();
        company.setCompanyId(UUID.randomUUID().toString());
        company.setCompanyCode(request.getCompanyCode());
        company.setCompanyName(request.getCompanyName());
        company.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        sysCompanyMapper.insert(company);
    }

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

    @Override
    public void delete(String companyId) {
        SysCompany company = getEnabledCompany(companyId);
        long usedCount = sysUserCompanyMapper.selectCount(new LambdaQueryWrapper<SysUserCompany>()
                .eq(SysUserCompany::getCompanyId, companyId));
        if (usedCount > 0) {
            throw new BizException("该单位已被用户关联，无法删除");
        }
        sysCompanyMapper.deleteById(companyId);
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
