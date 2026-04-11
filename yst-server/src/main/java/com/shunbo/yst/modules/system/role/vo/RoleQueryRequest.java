package com.shunbo.yst.modules.system.role.vo;

import com.shunbo.yst.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryRequest extends PageQuery {

    private String roleName;
    private String roleKey;
    private Integer status;
}
