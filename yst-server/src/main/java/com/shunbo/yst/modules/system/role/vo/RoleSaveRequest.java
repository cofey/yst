package com.shunbo.yst.modules.system.role.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RoleSaveRequest {

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    private Integer status;
    private List<String> menuIds;
}
