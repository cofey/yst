package com.shunbo.yst.component;

import com.alibaba.excel.EasyExcel;
import com.shunbo.yst.common.BizException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导出通用组件。
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * List<UserExcelRow> rows = buildRows(users);
 * excelExportComponent.writeRows(response, "用户列表", "用户",
 *         UserExcelRow.class, rows, "用户导出");
 * }</pre>
 */
@Component
public class ExcelExportComponent {

    /**
     * 将行数据写入响应输出流。
     */
    public <T> void writeRows(HttpServletResponse response, String fileName, String sheetName,
                              Class<T> rowType, List<T> rows, String bizName) {
        List<T> safeRows = rows == null ? new ArrayList<>() : rows;
        try {
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), rowType).sheet(sheetName).doWrite(safeRows);
        } catch (IOException e) {
            throw new BizException(buildExportErrorMessage(bizName) + ": " + e.getMessage());
        }
    }

    private String buildExportErrorMessage(String bizName) {
        if (bizName == null || bizName.isBlank()) {
            return "导出失败";
        }
        return bizName + "失败";
    }
}
