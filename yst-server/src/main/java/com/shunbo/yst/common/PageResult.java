package com.shunbo.yst.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageResult<T> {

    private List<T> records = Collections.emptyList();
    private long total;
    private long pageNum;
    private long pageSize;

    public PageResult(List<T> records, long total, long pageNum, long pageSize) {
        this.records = immutableCopy(records);
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public List<T> getRecords() {
        return new ArrayList<>(records);
    }

    public void setRecords(List<T> records) {
        this.records = immutableCopy(records);
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    private static <E> List<E> immutableCopy(List<E> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(source));
    }
}
