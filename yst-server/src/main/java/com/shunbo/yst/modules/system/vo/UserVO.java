package com.shunbo.yst.modules.system.vo;

import com.shunbo.yst.common.annotation.DateTimePattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UserVO 数据对象，用于承载业务数据。
 */
@Getter
@Setter
@Schema(description = "UserVO 对象")
public class UserVO {
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "公司ID集合")
    private List<String> companyIds = new ArrayList<>();
    @Schema(description = "公司名称集合")
    private List<String> companyNames = new ArrayList<>();
    @Schema(description = "角色ID集合")
    private List<String> roleIds = new ArrayList<>();
    @Schema(description = "角色名称集合")
    private List<String> roleNames = new ArrayList<>();
    @Schema(description = "状态")
    private Integer status;
    @DateTimePattern
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

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
        this.companyIds = copyList(companyIds);
    }

    /**

     * 获取getCompanyNames对应的数据。

     */

    public List<String> getCompanyNames() {
        return new ArrayList<>(companyNames);
    }

    /**

     * 设置setCompanyNames对应的数据。

     */

    public void setCompanyNames(List<String> companyNames) {
        this.companyNames = copyList(companyNames);
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
        this.roleIds = copyList(roleIds);
    }

    /**

     * 获取getRoleNames对应的数据。

     */

    public List<String> getRoleNames() {
        return new ArrayList<>(roleNames);
    }

    /**

     * 设置setRoleNames对应的数据。

     */

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = copyList(roleNames);
    }

    private static <T> List<T> copyList(List<T> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}
