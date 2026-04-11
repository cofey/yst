package com.shunbo.yst.modules.system.user.vo;

import com.shunbo.yst.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageQuery {

    private String username;
    private String nickname;
    private String phone;
    private String companyId;
    private String roleId;
    private Integer status;
}
