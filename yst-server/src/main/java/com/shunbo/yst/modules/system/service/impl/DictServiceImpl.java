package com.shunbo.yst.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.component.QuerySortComponent;

import com.shunbo.yst.modules.system.entity.SysDictData;
import com.shunbo.yst.modules.system.entity.SysDictType;
import com.shunbo.yst.modules.system.mapper.SysDictDataMapper;
import com.shunbo.yst.modules.system.mapper.SysDictTypeMapper;
import com.shunbo.yst.modules.system.service.DictCacheService;
import com.shunbo.yst.modules.system.service.DictService;
import com.shunbo.yst.modules.system.vo.DictDataQueryRequest;
import com.shunbo.yst.modules.system.vo.DictDataSaveRequest;
import com.shunbo.yst.modules.system.vo.DictDataUpdateRequest;
import com.shunbo.yst.modules.system.vo.DictOptionVO;
import com.shunbo.yst.modules.system.vo.DictTypeQueryRequest;
import com.shunbo.yst.modules.system.vo.DictTypeSaveRequest;
import com.shunbo.yst.modules.system.vo.DictTypeUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/**
 * DictServiceImpl 服务实现，负责执行业务流程与数据编排。
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private static final Map<String, SFunction<SysDictType, ?>> TYPE_SORT_COLUMNS = buildTypeSortColumns();
    private static final Map<String, SFunction<SysDictData, ?>> DATA_SORT_COLUMNS = buildDataSortColumns();

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataMapper sysDictDataMapper;
    private final DictCacheService dictCacheService;
    private final QuerySortComponent querySortComponent;

    /**
      * 执行listTypes相关处理。
      */
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

    /**
      * 执行createType相关处理。
      */
    @Override
    public void createType(DictTypeSaveRequest request) {
        checkTypeUnique(request.getDictType(), null);
        SysDictType entity = new SysDictType();
        entity.setDictId(UUID.randomUUID().toString());
        entity.setDictName(request.getDictName());
        entity.setDictType(request.getDictType());
        entity.setStatus(Objects.requireNonNullElse(request.getStatus(), 1));
        entity.setRemark(request.getRemark());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysDictTypeMapper.insert(entity);
        dictCacheService.evictDictOptions(entity.getDictType());
    }

    /**
      * 执行updateType相关处理。
      */
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

    /**
      * 执行deleteType相关处理。
      */
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

    /**
      * 执行listData相关处理。
      */
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

    /**
      * 执行createData相关处理。
      */
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

    /**
      * 执行updateData相关处理。
      */
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

    /**
      * 执行deleteData相关处理。
      */
    @Override
    public void deleteData(String dictCode) {
        SysDictData entity = sysDictDataMapper.selectById(dictCode);
        if (entity == null) {
            throw new BizException("字典数据不存在");
        }
        sysDictDataMapper.deleteById(dictCode);
        dictCacheService.evictDictOptions(entity.getDictType());
    }

    /**
      * 执行listEnabledOptionsByType相关处理。
      */
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

    /**
      * 执行clearCacheByType相关处理。
      */
    @Override
    public void clearCacheByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            throw new BizException("dictType 不能为空");
        }
        dictCacheService.evictDictOptions(dictType);
    }

    /**
      * 执行clearAllCache相关处理。
      */
    @Override
    public void clearAllCache() {
        dictCacheService.evictAllDictOptions();
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
        entity.setDictSort(Objects.requireNonNullElse(dictSort, 0));
        entity.setDictLabel(dictLabel);
        entity.setDictValue(dictValue);
        entity.setDictType(dictType);
        entity.setCssClass(cssClass);
        entity.setListClass(listClass);
        entity.setIsDefault("Y".equalsIgnoreCase(isDefault) ? "Y" : "N");
        entity.setStatus(Objects.requireNonNullElse(status, 1));
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
        querySortComponent.applySort(wrapper, query.normalizedSortField(), query.normalizedSortAsc(),
                SysDictType::getCreateTime, TYPE_SORT_COLUMNS);
    }

    private void applyDataSort(LambdaQueryWrapper<SysDictData> wrapper, DictDataQueryRequest query) {
        querySortComponent.applySort(wrapper, query.normalizedSortField(), query.normalizedSortAsc(),
                SysDictData::getCreateTime, DATA_SORT_COLUMNS);
    }

    private static Map<String, SFunction<SysDictType, ?>> buildTypeSortColumns() {
        Map<String, SFunction<SysDictType, ?>> sortColumns = new HashMap<>();
        sortColumns.put("dictName", SysDictType::getDictName);
        sortColumns.put("dict_name", SysDictType::getDictName);
        sortColumns.put("dictType", SysDictType::getDictType);
        sortColumns.put("dict_type", SysDictType::getDictType);
        sortColumns.put("status", SysDictType::getStatus);
        sortColumns.put("createTime", SysDictType::getCreateTime);
        sortColumns.put("create_time", SysDictType::getCreateTime);
        return sortColumns;
    }

    private static Map<String, SFunction<SysDictData, ?>> buildDataSortColumns() {
        Map<String, SFunction<SysDictData, ?>> sortColumns = new HashMap<>();
        sortColumns.put("dictSort", SysDictData::getDictSort);
        sortColumns.put("dict_sort", SysDictData::getDictSort);
        sortColumns.put("dictLabel", SysDictData::getDictLabel);
        sortColumns.put("dict_label", SysDictData::getDictLabel);
        sortColumns.put("dictValue", SysDictData::getDictValue);
        sortColumns.put("dict_value", SysDictData::getDictValue);
        sortColumns.put("status", SysDictData::getStatus);
        sortColumns.put("createTime", SysDictData::getCreateTime);
        sortColumns.put("create_time", SysDictData::getCreateTime);
        return sortColumns;
    }
}
