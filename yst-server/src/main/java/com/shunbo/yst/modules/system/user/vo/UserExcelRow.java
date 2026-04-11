package com.shunbo.yst.modules.system.user.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserExcelRow {

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("状态(1启用/0禁用)")
    private Integer status;
}
