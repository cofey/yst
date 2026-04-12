package com.shunbo.yst.modules.system.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserExcelRow 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "UserExcelRow 对象")
public class UserExcelRow {

    @ExcelProperty("用户名")
    @Schema(description = "用户名")
    private String username;

    @ExcelProperty("昵称")
    @Schema(description = "用户昵称")
    private String nickname;

    @ExcelProperty("邮箱")
    @Schema(description = "邮箱")
    private String email;

    @ExcelProperty("手机号")
    @Schema(description = "手机号")
    private String phone;

    @ExcelProperty("状态(1启用/0禁用)")
    @Schema(description = "状态")
    private Integer status;
}
