package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuTreeVO 数据对象，用于承载业务数据。
 */
@Getter
@Setter
@Schema(description = "MenuTreeVO 对象")
public class MenuTreeVO {
    @Schema(description = "菜单ID")
    private String menuId;
    @Schema(description = "父菜单ID")
    private String parentId;
    @Schema(description = "菜单名称")
    private String menuName;
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
    @Schema(description = "子菜单列表")
    private List<MenuTreeVO> children = new ArrayList<>();

    /**

     * 获取getChildren对应的数据。

     */

    public List<MenuTreeVO> getChildren() {
        return new ArrayList<>(children);
    }

    /**

     * 设置setChildren对应的数据。

     */

    public void setChildren(List<MenuTreeVO> children) {
        this.children = copyList(children);
    }

    private static <T> List<T> copyList(List<T> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}
