package com.shunbo.yst.component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.shunbo.yst.common.BizException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入通用组件。
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * List<UserExcelRow> rows = excelImportComponent.readRows(file, UserExcelRow.class, "用户导入");
 * for (UserExcelRow row : rows) {
 *     // 处理导入行
 * }
 * }</pre>
 */
@Component
public class ExcelImportComponent {

    /**
     * 读取 Excel 并转换为行对象列表。
     */
    public <T> List<T> readRows(MultipartFile file, Class<T> rowType, String bizName) {
        if (file == null || file.isEmpty()) {
            throw new BizException("请上传文件");
        }
        List<T> rows = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), rowType,
                    new PageReadListener<T>(rows::addAll)).sheet().doRead();
        } catch (IOException e) {
            throw new BizException(buildReadErrorMessage(bizName) + ": " + e.getMessage());
        }
        return rows;
    }

    private String buildReadErrorMessage(String bizName) {
        if (bizName == null || bizName.isBlank()) {
            return "读取Excel失败";
        }
        return bizName + "读取Excel失败";
    }
}
