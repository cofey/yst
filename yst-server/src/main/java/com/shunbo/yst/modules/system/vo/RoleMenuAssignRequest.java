package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * RoleMenuAssignRequest 数据对象，用于承载业务数据。
 */
@Getter
@Setter
@Schema(description = "RoleMenuAssignRequest 对象")
public class RoleMenuAssignRequest {
    @Schema(description = "菜单ID集合")
    private List<String> menuIds = new ArrayList<>();

    /**

     * 获取getMenuIds对应的数据。

     */

    public List<String> getMenuIds() {
        return new ArrayList<>(menuIds);
    }

    /**

     * 设置setMenuIds对应的数据。

     */

    public void setMenuIds(List<String> menuIds) {
        this.menuIds = menuIds == null ? new ArrayList<>() : new ArrayList<>(menuIds);
    }
}
