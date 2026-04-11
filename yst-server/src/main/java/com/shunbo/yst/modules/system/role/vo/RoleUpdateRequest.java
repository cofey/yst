package com.shunbo.yst.modules.system.role.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpdateRequest {

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    private Integer status;
}
