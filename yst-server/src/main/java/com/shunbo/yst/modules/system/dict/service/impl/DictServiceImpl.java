package com.shunbo.yst.modules.system.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.dict.entity.SysDictData;
import com.shunbo.yst.modules.system.dict.entity.SysDictType;
import com.shunbo.yst.modules.system.dict.mapper.SysDictDataMapper;
import com.shunbo.yst.modules.system.dict.mapper.SysDictTypeMapper;
import com.shunbo.yst.modules.system.dict.service.DictCacheService;
import com.shunbo.yst.modules.system.dict.service.DictService;
import com.shunbo.yst.modules.system.dict.vo.DictDataQueryRequest;
import com.shunbo.yst.modules.system.dict.vo.DictDataSaveRequest;
import com.shunbo.yst.modules.system.dict.vo.DictDataUpdateRequest;
import com.shunbo.yst.modules.system.dict.vo.DictOptionVO;
import com.shunbo.yst.modules.system.dict.vo.DictTypeQueryRequest;
import com.shunbo.yst.modules.system.dict.vo.DictTypeSaveRequest;
import com.shunbo.yst.modules.system.dict.vo.DictTypeUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataMapper sysDictDataMapper;
    private final DictCacheService dictCacheService;

    @Override
    public PageResult<SysDictType> listTypes(DictTypeQueryRequest query) {
        long pageNum = query.validPageNum();
        long pageSize = query.validPageSize();
        String dictName = query.getDictName();
        String dictType = query.getDictType();
        Integer status = query.getStatus();
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dictName), SysDictType::getDictName, dictName)
                .like(StringUtils.hasText(dictType), SysDictType::getDictType, dictType)
                .eq(status != null, SysDictType::getStatus, status);
        applyTypeSort(wrapper, query);
        Page<SysDictType> page = sysDictTypeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page);
    }

    @Override
    public void createType(DictTypeSaveRequest request) {
        checkTypeUnique(request.getDictType(), null);
        SysDictType entity = new SysDictType();
        entity.setDictId(UUID.randomUUID().toString());
        entity.setDictName(request.getDictName());
        entity.setDictType(request.getDictType());
        entity.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        entity.setRemark(request.getRemark());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysDictTypeMapper.insert(entity);
        dictCacheService.evictDictOptions(entity.getDictType());
    }

    @Override
    public void updateType(String dictId, DictTypeUpdateRequest request) {
        SysDictType entity = sysDictTypeMapper.selectById(dictId);
        if (entity == null) {
            throw new BizException("字典类型不存在");
        }
        String oldType = entity.getDictType();
        checkTypeUnique(request.getDictType(), dictId);
        entity.setDictName(request.getDictName());
        entity.setDictType(request.getDictType());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        entity.setRemark(request.getRemark());
        entity.setUpdateTime(LocalDateTime.now());
        sysDictTypeMapper.updateById(entity);
        if (!oldType.equals(request.getDictType())) {
            List<SysDictData> toUpdate = sysDictDataMapper.selectList(
                    new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, oldType)
            );
            for (SysDictData dictData : toUpdate) {
                dictData.setDictType(request.getDictType());
                dictData.setUpdateTime(LocalDateTime.now());
                sysDictDataMapper.updateById(dictData);
            }
        }
        dictCacheService.evictDictOptions(oldType);
        dictCacheService.evictDictOptions(request.getDictType());
    }

    @Override
    public void deleteType(String dictId) {
        SysDictType entity = sysDictTypeMapper.selectById(dictId);
        if (entity == null) {
            throw new BizException("字典类型不存在");
        }
        long dataCount = sysDictDataMapper.selectCount(
                new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, entity.getDictType())
        );
        if (dataCount > 0) {
            throw new BizException("字典类型下存在数据，无法删除");
        }
        sysDictTypeMapper.deleteById(dictId);
        dictCacheService.evictDictOptions(entity.getDictType());
    }

    @Override
    public PageResult<SysDictData> listData(DictDataQueryRequest query) {
        long pageNum = query.validPageNum();
        long pageSize = query.validPageSize();
        String dictType = query.getDictType();
        String dictLabel = query.getDictLabel();
        Integer status = query.getStatus();
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(dictType), SysDictData::getDictType, dictType)
                .like(StringUtils.hasText(dictLabel), SysDictData::getDictLabel, dictLabel)
                .eq(status != null, SysDictData::getStatus, status);
        applyDataSort(wrapper, query);
        Page<SysDictData> page = sysDictDataMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page);
    }

    @Override
    public void createData(DictDataSaveRequest request) {
        ensureTypeExists(request.getDictType());
        checkDataValueUnique(request.getDictType(), request.getDictValue(), null);
        SysDictData entity = new SysDictData();
        entity.setDictCode(UUID.randomUUID().toString());
        fillDataEntity(entity, request.getDictSort(), request.getDictLabel(), request.getDictValue(), request.getDictType(),
                request.getCssClass(), request.getListClass(), request.getIsDefault(), request.getStatus(), request.getRemark());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysDictDataMapper.insert(entity);
        dictCacheService.evictDictOptions(request.getDictType());
    }

    @Override
    public void updateData(String dictCode, DictDataUpdateRequest request) {
        SysDictData entity = sysDictDataMapper.selectById(dictCode);
        if (entity == null) {
            throw new BizException("字典数据不存在");
        }
        String oldType = entity.getDictType();
        ensureTypeExists(request.getDictType());
        checkDataValueUnique(request.getDictType(), request.getDictValue(), dictCode);
        fillDataEntity(entity, request.getDictSort(), request.getDictLabel(), request.getDictValue(), request.getDictType(),
                request.getCssClass(), request.getListClass(), request.getIsDefault(), request.getStatus(), request.getRemark());
        entity.setUpdateTime(LocalDateTime.now());
        sysDictDataMapper.updateById(entity);
        dictCacheService.evictDictOptions(oldType);
        dictCacheService.evictDictOptions(request.getDictType());
    }

    @Override
    public void deleteData(String dictCode) {
        SysDictData entity = sysDictDataMapper.selectById(dictCode);
        if (entity == null) {
            throw new BizException("字典数据不存在");
        }
        sysDictDataMapper.deleteById(dictCode);
        dictCacheService.evictDictOptions(entity.getDictType());
    }

    @Override
    public List<DictOptionVO> listEnabledOptionsByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            throw new BizException("dictType 不能为空");
        }
        List<DictOptionVO> cache = dictCacheService.getDictOptions(dictType);
        if (cache != null) {
            return cache;
        }
        List<SysDictData> rows = sysDictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, 1)
                        .orderByAsc(SysDictData::getDictSort)
                        .orderByAsc(SysDictData::getDictCode)
        );
        List<DictOptionVO> options = rows.stream().map(this::toOption).collect(Collectors.toList());
        dictCacheService.putDictOptions(dictType, options);
        return options;
    }

    private void checkTypeUnique(String dictType, String excludeDictId) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType);
        if (excludeDictId != null) {
            wrapper.ne(SysDictType::getDictId, excludeDictId);
        }
        SysDictType exists = sysDictTypeMapper.selectOne(wrapper.last("limit 1"));
        if (exists != null) {
            throw new BizException("字典类型已存在");
        }
    }

    private void checkDataValueUnique(String dictType, String dictValue, String excludeDictCode) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue);
        if (excludeDictCode != null) {
            wrapper.ne(SysDictData::getDictCode, excludeDictCode);
        }
        SysDictData exists = sysDictDataMapper.selectOne(wrapper.last("limit 1"));
        if (exists != null) {
            throw new BizException("同字典类型下键值已存在");
        }
    }

    private void ensureTypeExists(String dictType) {
        SysDictType type = sysDictTypeMapper.selectOne(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType)
                .last("limit 1"));
        if (type == null) {
            throw new BizException("字典类型不存在");
        }
    }

    private void fillDataEntity(SysDictData entity, Integer dictSort, String dictLabel, String dictValue,
                                String dictType, String cssClass, String listClass, String isDefault,
                                Integer status, String remark) {
        entity.setDictSort(dictSort == null ? 0 : dictSort);
        entity.setDictLabel(dictLabel);
        entity.setDictValue(dictValue);
        entity.setDictType(dictType);
        entity.setCssClass(cssClass);
        entity.setListClass(listClass);
        entity.setIsDefault("Y".equalsIgnoreCase(isDefault) ? "Y" : "N");
        entity.setStatus(status == null ? 1 : status);
        entity.setRemark(remark);
    }

    private DictOptionVO toOption(SysDictData data) {
        DictOptionVO option = new DictOptionVO();
        option.setLabel(data.getDictLabel());
        option.setValue(data.getDictValue());
        option.setCssClass(data.getCssClass());
        option.setListClass(data.getListClass());
        option.setIsDefault(data.getIsDefault());
        return option;
    }

    private void applyTypeSort(LambdaQueryWrapper<SysDictType> wrapper, DictTypeQueryRequest query) {
        boolean asc = query.normalizedSortAsc();
        String field = query.normalizedSortField();
        switch (field) {
            case "dictName":
            case "dict_name":
                wrapper.orderBy(true, asc, SysDictType::getDictName);
                break;
            case "dictType":
            case "dict_type":
                wrapper.orderBy(true, asc, SysDictType::getDictType);
                break;
            case "status":
                wrapper.orderBy(true, asc, SysDictType::getStatus);
                break;
            case "createTime":
            case "create_time":
                wrapper.orderBy(true, asc, SysDictType::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(SysDictType::getCreateTime);
                break;
        }
    }

    private void applyDataSort(LambdaQueryWrapper<SysDictData> wrapper, DictDataQueryRequest query) {
        boolean asc = query.normalizedSortAsc();
        String field = query.normalizedSortField();
        switch (field) {
            case "dictSort":
            case "dict_sort":
                wrapper.orderBy(true, asc, SysDictData::getDictSort);
                break;
            case "dictLabel":
            case "dict_label":
                wrapper.orderBy(true, asc, SysDictData::getDictLabel);
                break;
            case "dictValue":
            case "dict_value":
                wrapper.orderBy(true, asc, SysDictData::getDictValue);
                break;
            case "status":
                wrapper.orderBy(true, asc, SysDictData::getStatus);
                break;
            case "createTime":
            case "create_time":
                wrapper.orderBy(true, asc, SysDictData::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(SysDictData::getCreateTime);
                break;
        }
    }
}
