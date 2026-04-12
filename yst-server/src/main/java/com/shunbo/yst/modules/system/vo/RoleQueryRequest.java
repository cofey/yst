package com.shunbo.yst.modules.system.vo;

import com.shunbo.yst.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RoleQueryRequest 数据对象，用于承载业务数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "RoleQueryRequest 对象")
public class RoleQueryRequest extends PageQuery {

    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色权限标识")
    private String roleKey;
    @Schema(description = "状态")
    private Integer status;
}
