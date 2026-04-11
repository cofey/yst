package com.shunbo.yst.modules.system.dict.service;

import com.shunbo.yst.modules.system.dict.vo.DictOptionVO;

import java.util.List;

public interface DictCacheService {

    List<DictOptionVO> getDictOptions(String dictType);

    void putDictOptions(String dictType, List<DictOptionVO> options);

    void evictDictOptions(String dictType);
}
