package com.shunbo.yst.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通用排序组件，用于分页查询的安全排序处理。
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * querySortComponent.applySort(wrapper, query.normalizedSortField(), query.normalizedSortAsc(),
 *         SysUser::getCreateTime, LIST_SORT_COLUMNS);
 * }</pre>
 */
@Component
public class QuerySortComponent {

    /**
     * 根据可支持字段映射应用排序，并在字段不合法时走默认排序列兜底。
     *
     * <p>当 {@code field} 不在 {@code supportedColumns} 中时，自动按 {@code defaultColumn} 倒序排序。</p>
     */
    public <T> void applySort(LambdaQueryWrapper<T> wrapper, String field, boolean asc,
                              SFunction<T, ?> defaultColumn, Map<String, SFunction<T, ?>> supportedColumns) {
        SFunction<T, ?> sortColumn = supportedColumns.get(field);
        if (sortColumn == null) {
            wrapper.orderByDesc(defaultColumn);
            return;
        }
        wrapper.orderBy(true, asc, sortColumn);
    }
}
