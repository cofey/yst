package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * MenuSaveRequest 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "MenuSaveRequest 对象")
public class MenuSaveRequest {

    @Schema(description = "父菜单ID")
    private String parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名称")
    private String menuName;

    @NotBlank(message = "菜单类型不能为空")
    @Schema(description = "菜单类型")
    private String menuType;

    @Schema(description = "路由路径")
    private String path;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "权限标识")
    private String perms;
    @Schema(description = "显示状态")
    private Integer visible;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "排序值")
    private Integer sort;
}
