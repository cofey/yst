package com.shunbo.yst.modules.system.role.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuAssignRequest {
    private List<String> menuIds;
}
