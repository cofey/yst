package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RoleUpdateRequest 数据对象，用于承载业务数据。
 */
@Data
@Schema(description = "RoleUpdateRequest 对象")
public class RoleUpdateRequest {

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    @Schema(description = "角色权限标识")
    private String roleKey;

    @Schema(description = "状态")
    private Integer status;
}
