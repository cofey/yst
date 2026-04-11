package com.shunbo.yst.modules.system.dict.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.dict.entity.SysDictData;
import com.shunbo.yst.modules.system.dict.entity.SysDictType;
import com.shunbo.yst.modules.system.dict.vo.DictDataQueryRequest;
import com.shunbo.yst.modules.system.dict.vo.DictDataSaveRequest;
import com.shunbo.yst.modules.system.dict.vo.DictDataUpdateRequest;
import com.shunbo.yst.modules.system.dict.vo.DictOptionVO;
import com.shunbo.yst.modules.system.dict.vo.DictTypeQueryRequest;
import com.shunbo.yst.modules.system.dict.vo.DictTypeSaveRequest;
import com.shunbo.yst.modules.system.dict.vo.DictTypeUpdateRequest;

import java.util.List;

public interface DictService {

    PageResult<SysDictType> listTypes(DictTypeQueryRequest query);

    void createType(DictTypeSaveRequest request);

    void updateType(String dictId, DictTypeUpdateRequest request);

    void deleteType(String dictId);

    PageResult<SysDictData> listData(DictDataQueryRequest query);

    void createData(DictDataSaveRequest request);

    void updateData(String dictCode, DictDataUpdateRequest request);

    void deleteData(String dictCode);

    List<DictOptionVO> listEnabledOptionsByType(String dictType);

    void clearCacheByType(String dictType);

    void clearAllCache();
}
