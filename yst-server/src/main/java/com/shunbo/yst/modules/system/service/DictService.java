package com.shunbo.yst.modules.system.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.entity.SysDictData;
import com.shunbo.yst.modules.system.entity.SysDictType;
import com.shunbo.yst.modules.system.vo.DictDataQueryRequest;
import com.shunbo.yst.modules.system.vo.DictDataSaveRequest;
import com.shunbo.yst.modules.system.vo.DictDataUpdateRequest;
import com.shunbo.yst.modules.system.vo.DictOptionVO;
import com.shunbo.yst.modules.system.vo.DictTypeQueryRequest;
import com.shunbo.yst.modules.system.vo.DictTypeSaveRequest;
import com.shunbo.yst.modules.system.vo.DictTypeUpdateRequest;

import java.util.List;

/**
 * DictService 服务接口，定义业务能力与契约。
 */
public interface DictService {

    /**
     * 执行listTypes相关处理。
     */

    PageResult<SysDictType> listTypes(DictTypeQueryRequest query);

    /**
     * 执行createType相关处理。
     */

    void createType(DictTypeSaveRequest request);

    /**
     * 执行updateType相关处理。
     */

    void updateType(String dictId, DictTypeUpdateRequest request);

    /**
     * 执行deleteType相关处理。
     */

    void deleteType(String dictId);

    /**
     * 执行listData相关处理。
     */

    PageResult<SysDictData> listData(DictDataQueryRequest query);

    /**
     * 执行createData相关处理。
     */

    void createData(DictDataSaveRequest request);

    /**
     * 执行updateData相关处理。
     */

    void updateData(String dictCode, DictDataUpdateRequest request);

    /**
     * 执行deleteData相关处理。
     */

    void deleteData(String dictCode);

    /**
     * 执行listEnabledOptionsByType相关处理。
     */

    List<DictOptionVO> listEnabledOptionsByType(String dictType);

    /**
     * 执行clearCacheByType相关处理。
     */

    void clearCacheByType(String dictType);

    /**
     * 执行clearAllCache相关处理。
     */

    void clearAllCache();
}
