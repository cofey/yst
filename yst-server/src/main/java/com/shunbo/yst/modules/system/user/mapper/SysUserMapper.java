package com.shunbo.yst.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shunbo.yst.modules.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("""
            SELECT DISTINCT r.role_key
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.role_id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
            """)
    List<String> selectRoleKeysByUserId(@Param("userId") String userId);

    @Select("""
            SELECT DISTINCT m.perms
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.role_id
            JOIN sys_role_menu rm ON r.role_id = rm.role_id
            JOIN sys_menu m ON rm.menu_id = m.menu_id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND m.status = 1
              AND m.menu_type = 'F'
              AND m.perms IS NOT NULL
              AND m.perms <> ''
            """)
    List<String> selectPermissionsByUserId(@Param("userId") String userId);

    @Select("""
            SELECT role_id
            FROM sys_user_role
            WHERE user_id = #{userId}
            """)
    List<String> selectRoleIdsByUserId(@Param("userId") String userId);

    @Select("""
            SELECT company_id
            FROM sys_user_company
            WHERE user_id = #{userId}
            """)
    List<String> selectCompanyIdsByUserId(@Param("userId") String userId);

    @Select("""
            SELECT r.role_name
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.role_id
            WHERE ur.user_id = #{userId}
            """)
    List<String> selectRoleNamesByUserId(@Param("userId") String userId);

    @Select("""
            SELECT c.company_name
            FROM sys_user_company uc
            JOIN sys_company c ON uc.company_id = c.company_id
            WHERE uc.user_id = #{userId}
            """)
    List<String> selectCompanyNamesByUserId(@Param("userId") String userId);

    @Select("""
            SELECT DISTINCT user_id
            FROM sys_user_role
            WHERE role_id = #{roleId}
            """)
    List<String> selectUserIdsByRoleId(@Param("roleId") String roleId);

    @Select("""
            SELECT DISTINCT user_id
            FROM sys_user_company
            WHERE company_id = #{companyId}
            """)
    List<String> selectUserIdsByCompanyId(@Param("companyId") String companyId);
}
