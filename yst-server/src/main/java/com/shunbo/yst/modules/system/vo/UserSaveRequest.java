package com.shunbo.yst.modules.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * UserSaveRequest 数据对象，用于承载业务数据。
 */
@Getter
@Setter
@Schema(description = "UserSaveRequest 对象")
public class UserSaveRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Schema(description = "用户昵称")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "公司ID集合")
    private List<String> companyIds = new ArrayList<>();
    @Schema(description = "角色ID集合")
    private List<String> roleIds = new ArrayList<>();

    @Schema(description = "密码")
    private String password;

    /**

     * 获取getCompanyIds对应的数据。

     */

    public List<String> getCompanyIds() {
        return new ArrayList<>(companyIds);
    }

    /**

     * 设置setCompanyIds对应的数据。

     */

    public void setCompanyIds(List<String> companyIds) {
        this.companyIds = companyIds == null ? new ArrayList<>() : new ArrayList<>(companyIds);
    }

    /**

     * 获取getRoleIds对应的数据。

     */

    public List<String> getRoleIds() {
        return new ArrayList<>(roleIds);
    }

    /**

     * 设置setRoleIds对应的数据。

     */

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds == null ? new ArrayList<>() : new ArrayList<>(roleIds);
    }
}
