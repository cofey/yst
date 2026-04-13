package com.shunbo.yst.modules.system.controller;

import com.shunbo.yst.common.ApiResponse;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.entity.SysDictData;
import com.shunbo.yst.modules.system.entity.SysDictType;
import com.shunbo.yst.modules.system.service.DictService;
import com.shunbo.yst.modules.system.vo.DictDataQueryRequest;
import com.shunbo.yst.modules.system.vo.DictDataSaveRequest;
import com.shunbo.yst.modules.system.vo.DictDataUpdateRequest;
import com.shunbo.yst.modules.system.vo.DictOptionVO;
import com.shunbo.yst.modules.system.vo.DictTypeQueryRequest;
import com.shunbo.yst.modules.system.vo.DictTypeSaveRequest;
import com.shunbo.yst.modules.system.vo.DictTypeUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * DictController 控制器，负责处理对应模块的接口请求。
 */
@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
@Tag(name = "DictController", description = "DictController 控制器")
public class DictController {

    private final DictService dictService;

    /**
     * 执行listTypes相关处理。
     */
    @GetMapping("/types")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @Operation(summary = "listTypes")
    public ApiResponse<PageResult<SysDictType>> listTypes(@ModelAttribute DictTypeQueryRequest query) {
        return ApiResponse.ok(dictService.listTypes(query));
    }

    /**
     * 执行createType相关处理。
     */
    @PostMapping("/types")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Operation(summary = "createType")
    public ApiResponse<Void> createType(@RequestBody @Valid DictTypeSaveRequest request) {
        dictService.createType(request);
        return ApiResponse.ok();
    }

    /**
     * 执行updateType相关处理。
     */
    @PutMapping("/types/{dictId}")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Operation(summary = "updateType")
    public ApiResponse<Void> updateType(@PathVariable String dictId, @RequestBody @Valid DictTypeUpdateRequest request) {
        dictService.updateType(dictId, request);
        return ApiResponse.ok();
    }

    /**
     * 执行deleteType相关处理。
     */
    @DeleteMapping("/types/{dictId}")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Operation(summary = "deleteType")
    public ApiResponse<Void> deleteType(@PathVariable String dictId) {
        dictService.deleteType(dictId);
        return ApiResponse.ok();
    }

    /**
     * 执行listData相关处理。
     */
    @GetMapping("/data")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @Operation(summary = "listData")
    public ApiResponse<PageResult<SysDictData>> listData(@ModelAttribute DictDataQueryRequest query) {
        return ApiResponse.ok(dictService.listData(query));
    }

    /**
     * 执行createData相关处理。
     */
    @PostMapping("/data")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Operation(summary = "createData")
    public ApiResponse<Void> createData(@RequestBody @Valid DictDataSaveRequest request) {
        dictService.createData(request);
        return ApiResponse.ok();
    }

    /**
     * 执行updateData相关处理。
     */
    @PutMapping("/data/{dictCode}")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Operation(summary = "updateData")
    public ApiResponse<Void> updateData(@PathVariable String dictCode, @RequestBody @Valid DictDataUpdateRequest request) {
        dictService.updateData(dictCode, request);
        return ApiResponse.ok();
    }

    /**
     * 执行deleteData相关处理。
     */
    @DeleteMapping("/data/{dictCode}")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Operation(summary = "deleteData")
    public ApiResponse<Void> deleteData(@PathVariable String dictCode) {
        dictService.deleteData(dictCode);
        return ApiResponse.ok();
    }

    /**
     * 执行listByType相关处理。
     */
    @GetMapping("/data/type/{dictType}")
    @Operation(summary = "listByType")
    public ApiResponse<List<DictOptionVO>> listByType(@PathVariable String dictType) {
        return ApiResponse.ok(dictService.listEnabledOptionsByType(dictType));
    }

    /**
     * 执行clearCacheByType相关处理。
     */
    @DeleteMapping("/cache/type/{dictType}")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Operation(summary = "clearCacheByType")
    public ApiResponse<Void> clearCacheByType(@PathVariable String dictType) {
        dictService.clearCacheByType(dictType);
        return ApiResponse.ok();
    }

    /**
     * 执行clearAllCache相关处理。
     */
    @DeleteMapping("/cache/all")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Operation(summary = "clearAllCache")
    public ApiResponse<Void> clearAllCache() {
        dictService.clearAllCache();
        return ApiResponse.ok();
    }
}
