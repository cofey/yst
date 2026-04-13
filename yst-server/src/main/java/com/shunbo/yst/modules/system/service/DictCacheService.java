package com.shunbo.yst.modules.system.service;

import com.shunbo.yst.modules.system.vo.DictOptionVO;

import java.util.List;

/**
 * DictCacheService 服务接口，定义业务能力与契约。
 */
public interface DictCacheService {

    /**
     * 执行getDictOptions相关处理。
     */

    List<DictOptionVO> getDictOptions(String dictType);

    /**
     * 执行putDictOptions相关处理。
     */

    void putDictOptions(String dictType, List<DictOptionVO> options);

    /**
     * 执行evictDictOptions相关处理。
     */

    void evictDictOptions(String dictType);

    /**
     * 执行evictAllDictOptions相关处理。
     */

    void evictAllDictOptions();
}
