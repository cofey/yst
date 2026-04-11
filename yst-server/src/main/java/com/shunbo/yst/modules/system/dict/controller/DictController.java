package com.shunbo.yst.modules.system.dict.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.dict.entity.SysDictData;
import com.shunbo.yst.modules.system.dict.entity.SysDictType;
import com.shunbo.yst.modules.system.dict.service.DictService;
import com.shunbo.yst.modules.system.dict.vo.DictDataQueryRequest;
import com.shunbo.yst.modules.system.dict.vo.DictDataSaveRequest;
import com.shunbo.yst.modules.system.dict.vo.DictDataUpdateRequest;
import com.shunbo.yst.modules.system.dict.vo.DictOptionVO;
import com.shunbo.yst.modules.system.dict.vo.DictTypeQueryRequest;
import com.shunbo.yst.modules.system.dict.vo.DictTypeSaveRequest;
import com.shunbo.yst.modules.system.dict.vo.DictTypeUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/types")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    public ApiResponse<PageResult<SysDictType>> listTypes(@ModelAttribute DictTypeQueryRequest query) {
        return ApiResponse.ok(dictService.listTypes(query));
    }

    @PostMapping("/types")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    public ApiResponse<Void> createType(@RequestBody @Valid DictTypeSaveRequest request) {
        dictService.createType(request);
        return ApiResponse.ok();
    }

    @PutMapping("/types/{dictId}")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    public ApiResponse<Void> updateType(@PathVariable String dictId, @RequestBody @Valid DictTypeUpdateRequest request) {
        dictService.updateType(dictId, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/types/{dictId}")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    public ApiResponse<Void> deleteType(@PathVariable String dictId) {
        dictService.deleteType(dictId);
        return ApiResponse.ok();
    }

    @GetMapping("/data")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    public ApiResponse<PageResult<SysDictData>> listData(@ModelAttribute DictDataQueryRequest query) {
        return ApiResponse.ok(dictService.listData(query));
    }

    @PostMapping("/data")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    public ApiResponse<Void> createData(@RequestBody @Valid DictDataSaveRequest request) {
        dictService.createData(request);
        return ApiResponse.ok();
    }

    @PutMapping("/data/{dictCode}")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    public ApiResponse<Void> updateData(@PathVariable String dictCode, @RequestBody @Valid DictDataUpdateRequest request) {
        dictService.updateData(dictCode, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/data/{dictCode}")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    public ApiResponse<Void> deleteData(@PathVariable String dictCode) {
        dictService.deleteData(dictCode);
        return ApiResponse.ok();
    }

    @GetMapping("/data/type/{dictType}")
    public ApiResponse<List<DictOptionVO>> listByType(@PathVariable String dictType) {
        return ApiResponse.ok(dictService.listEnabledOptionsByType(dictType));
    }
}
