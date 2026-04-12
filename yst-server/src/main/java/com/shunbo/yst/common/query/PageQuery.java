package com.shunbo.yst.common.query;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Data
public class PageQuery {

    private Long pageNum = 1L;
    private Long pageSize = 10L;

    // 新协议
    private String sortField;
    private String sortOrder;
    // 兼容若依协议
    private String orderByColumn;
    private String isAsc;

    public long validPageNum() {
        return pageNum == null || pageNum < 1 ? 1L : pageNum;
    }

    public long validPageSize() {
        return pageSize == null || pageSize < 1 ? 10L : pageSize;
    }

    public String normalizedSortField() {
        if (StringUtils.hasText(sortField)) {
            return sortField.trim();
        }
        if (StringUtils.hasText(orderByColumn)) {
            return orderByColumn.trim();
        }
        return "";
    }

    public boolean normalizedSortAsc() {
        String order = sortOrder;
        if (!StringUtils.hasText(order)) {
            order = isAsc;
        }
        if (!StringUtils.hasText(order)) {
            return false;
        }
        String normalized = order.trim().toLowerCase(Locale.ROOT);
        return "asc".equals(normalized) || "ascending".equals(normalized) || "ascend".equals(normalized);
    }
}
