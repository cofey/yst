package com.shunbo.yst.modules.system.vo;

import com.shunbo.yst.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserQueryRequest 数据对象，用于承载业务数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "UserQueryRequest 对象")
public class UserQueryRequest extends PageQuery {

    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "公司ID")
    private String companyId;
    @Schema(description = "角色ID")
    private String roleId;
    @Schema(description = "状态")
    private Integer status;
}
