package com.shunbo.yst.modules.system.menu.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuSaveRequest {

    private String parentId;

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    private String path;
    private String component;
    private String icon;
    private String perms;
    private Integer visible;
    private Integer status;
    private Integer sort;
}
