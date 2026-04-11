package com.shunbo.yst.modules.system.dict.service.impl;

import com.shunbo.yst.common.BizException;
import com.shunbo.yst.modules.system.dict.entity.SysDictData;
import com.shunbo.yst.modules.system.dict.entity.SysDictType;
import com.shunbo.yst.modules.system.dict.mapper.SysDictDataMapper;
import com.shunbo.yst.modules.system.dict.mapper.SysDictTypeMapper;
import com.shunbo.yst.modules.system.dict.service.DictCacheService;
import com.shunbo.yst.modules.system.dict.vo.DictOptionVO;
import com.shunbo.yst.modules.system.dict.vo.DictTypeSaveRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictServiceImplTest {

    @Mock
    private SysDictTypeMapper sysDictTypeMapper;

    @Mock
    private SysDictDataMapper sysDictDataMapper;

    @Mock
    private DictCacheService dictCacheService;

    @InjectMocks
    private DictServiceImpl dictService;

    @Test
    void createTypeShouldInsertAndEvictCache() {
        DictTypeSaveRequest request = new DictTypeSaveRequest();
        request.setDictName("状态");
        request.setDictType("sys_common_status");
        request.setStatus(1);

        when(sysDictTypeMapper.selectOne(any())).thenReturn(null);

        dictService.createType(request);

        ArgumentCaptor<SysDictType> captor = ArgumentCaptor.forClass(SysDictType.class);
        verify(sysDictTypeMapper).insert(captor.capture());
        SysDictType saved = captor.getValue();
        assertEquals("状态", saved.getDictName());
        assertEquals("sys_common_status", saved.getDictType());
        assertEquals(1, saved.getStatus());
        verify(dictCacheService).evictDictOptions("sys_common_status");
    }

    @Test
    void createTypeShouldThrowWhenTypeDuplicated() {
        DictTypeSaveRequest request = new DictTypeSaveRequest();
        request.setDictName("状态");
        request.setDictType("sys_common_status");

        when(sysDictTypeMapper.selectOne(any())).thenReturn(new SysDictType());

        assertThrows(BizException.class, () -> dictService.createType(request));
        verify(sysDictTypeMapper, never()).insert(any(SysDictType.class));
    }

    @Test
    void listEnabledOptionsShouldReturnCacheWhenHit() {
        DictOptionVO option = new DictOptionVO();
        option.setLabel("启用");
        option.setValue("1");
        when(dictCacheService.getDictOptions("sys_common_status")).thenReturn(List.of(option));

        List<DictOptionVO> result = dictService.listEnabledOptionsByType("sys_common_status");

        assertEquals(1, result.size());
        assertEquals("启用", result.get(0).getLabel());
        verify(sysDictDataMapper, never()).selectList(any());
    }

    @Test
    void listEnabledOptionsShouldLoadFromDbAndWriteCache() {
        SysDictData row = new SysDictData();
        row.setDictLabel("启用");
        row.setDictValue("1");
        row.setCssClass("");
        row.setListClass("success");
        row.setIsDefault("Y");

        when(dictCacheService.getDictOptions("sys_common_status")).thenReturn(null);
        when(sysDictDataMapper.selectList(any())).thenReturn(List.of(row));

        List<DictOptionVO> result = dictService.listEnabledOptionsByType("sys_common_status");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getValue());
        verify(dictCacheService).putDictOptions(eq("sys_common_status"), any());
    }
}
