package com.shunbo.yst.modules.system.menu.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuTreeVO {
    private String menuId;
    private String parentId;
    private String menuName;
    private String menuType;
    private String path;
    private String component;
    private String icon;
    private String perms;
    private Integer visible;
    private Integer status;
    private Integer sort;
    private List<MenuTreeVO> children;
}
