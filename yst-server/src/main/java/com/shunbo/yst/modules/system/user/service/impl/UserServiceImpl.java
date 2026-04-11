package com.shunbo.yst.modules.system.user.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shunbo.yst.common.BizException;
import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.system.user.entity.SysUser;
import com.shunbo.yst.modules.system.user.entity.SysUserCompany;
import com.shunbo.yst.modules.system.user.entity.SysUserRole;
import com.shunbo.yst.modules.system.user.mapper.SysUserCompanyMapper;
import com.shunbo.yst.modules.system.user.mapper.SysUserMapper;
import com.shunbo.yst.modules.system.user.mapper.SysUserRoleMapper;
import com.shunbo.yst.security.CurrentUserUtils;
import com.shunbo.yst.security.AuthPermissionService;
import com.shunbo.yst.security.JwtTokenUtil;
import com.shunbo.yst.security.LoginUser;
import com.shunbo.yst.modules.system.user.service.UserService;
import com.shunbo.yst.modules.auth.vo.AuthInfoVO;
import com.shunbo.yst.modules.auth.vo.LoginRequest;
import com.shunbo.yst.modules.auth.vo.LoginResponse;
import com.shunbo.yst.modules.system.user.vo.UserExcelRow;
import com.shunbo.yst.modules.system.user.vo.UserQueryRequest;
import com.shunbo.yst.modules.system.user.vo.UserSaveRequest;
import com.shunbo.yst.modules.system.user.vo.UserUpdateRequest;
import com.shunbo.yst.modules.system.user.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PASSWORD = "yst.12345";

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserCompanyMapper sysUserCompanyMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthPermissionService authPermissionService;

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null || !passwordMatched(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BizException("用户已被禁用");
        }
        // 兼容历史数据：如果数据库中不是 bcrypt 哈希，首次成功登录后自动升级。
        if (!isBcryptHash(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setUpdateTime(LocalDateTime.now());
            sysUserMapper.updateById(user);
            authPermissionService.evictLoginUser(user.getUserId());
        }
        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername());
        return new LoginResponse(user.getUserId(), token, user.getUsername());
    }

    @Override
    public AuthInfoVO currentAuthInfo() {
        LoginUser loginUser = CurrentUserUtils.getCurrentUser();
        AuthInfoVO authInfoVO = new AuthInfoVO();
        authInfoVO.setUserId(loginUser.getUserId());
        authInfoVO.setUsername(loginUser.getUsername());
        authInfoVO.setRoles(new HashSet<>(loginUser.getRoles()));
        authInfoVO.setPermissions(new HashSet<>(loginUser.getPermissions()));
        return authInfoVO;
    }

    @Override
    public PageResult<UserVO> list(UserQueryRequest query) {
        long pageNum = query.validPageNum();
        long pageSize = query.validPageSize();
        String username = query.getUsername();
        String nickname = query.getNickname();
        String phone = query.getPhone();
        String companyId = query.getCompanyId();
        String roleId = query.getRoleId();
        Integer status = query.getStatus();

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username);
        wrapper.like(StringUtils.hasText(nickname), SysUser::getNickname, nickname);
        wrapper.like(StringUtils.hasText(phone), SysUser::getPhone, phone);
        wrapper.eq(status != null, SysUser::getStatus, status);
        applyListSort(wrapper, query);
        if (StringUtils.hasText(companyId)) {
            List<String> userIds = sysUserMapper.selectUserIdsByCompanyId(companyId);
            if (userIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0, pageNum, pageSize);
            }
            wrapper.in(SysUser::getUserId, userIds);
        }
        if (StringUtils.hasText(roleId)) {
            List<String> userIds = sysUserMapper.selectUserIdsByRoleId(roleId);
            if (userIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0, pageNum, pageSize);
            }
            wrapper.in(SysUser::getUserId, userIds);
        }
        Page<SysUser> queryPage = new Page<>(pageNum, pageSize);
        Page<SysUser> userPage = sysUserMapper.selectPage(queryPage, wrapper);
        List<UserVO> result = new ArrayList<>();
        for (SysUser user : userPage.getRecords()) {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            vo.setRoleIds(sysUserMapper.selectRoleIdsByUserId(user.getUserId()));
            vo.setCompanyIds(sysUserMapper.selectCompanyIdsByUserId(user.getUserId()));
            vo.setRoleNames(sysUserMapper.selectRoleNamesByUserId(user.getUserId()));
            vo.setCompanyNames(sysUserMapper.selectCompanyNamesByUserId(user.getUserId()));
            result.add(vo);
        }
        return new PageResult<>(result, userPage.getTotal(), pageNum, pageSize);
    }

    private void applyListSort(LambdaQueryWrapper<SysUser> wrapper, UserQueryRequest query) {
        boolean asc = query.normalizedSortAsc();
        String field = query.normalizedSortField();
        switch (field) {
            case "username":
                wrapper.orderBy(true, asc, SysUser::getUsername);
                break;
            case "nickname":
                wrapper.orderBy(true, asc, SysUser::getNickname);
                break;
            case "status":
                wrapper.orderBy(true, asc, SysUser::getStatus);
                break;
            case "createTime":
            case "create_time":
                wrapper.orderBy(true, asc, SysUser::getCreateTime);
                break;
            default:
                wrapper.orderByDesc(SysUser::getCreateTime);
                break;
        }
    }

    @Override
    public void create(UserSaveRequest request) {
        checkUsernameUnique(request.getUsername(), null);
        SysUser user = new SysUser();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        String rawPassword = StringUtils.hasText(request.getPassword()) ? request.getPassword() : DEFAULT_PASSWORD;
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        saveUserRoles(user.getUserId(), request.getRoleIds());
        saveUserCompanies(user.getUserId(), request.getCompanyIds());
    }

    @Override
    public void update(String userId, UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        saveUserRoles(userId, request.getRoleIds());
        saveUserCompanies(userId, request.getCompanyIds());
        authPermissionService.evictLoginUser(userId);
    }

    @Override
    public void delete(String userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        clearUserRelations(userId);
        sysUserMapper.deleteById(userId);
        authPermissionService.evictLoginUser(userId);
    }

    @Override
    public void importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("请上传文件");
        }
        List<UserExcelRow> rows = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), UserExcelRow.class,
                    new PageReadListener<UserExcelRow>(rows::addAll)).sheet().doRead();
        } catch (IOException e) {
            throw new BizException("读取Excel失败: " + e.getMessage());
        }

        List<String> updatedUserIds = new ArrayList<>();
        for (UserExcelRow row : rows) {
            if (!StringUtils.hasText(row.getUsername()) || !StringUtils.hasText(row.getNickname())) {
                continue;
            }
            SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, row.getUsername()));
            boolean exists = user != null;
            if (user == null) {
                user = new SysUser();
                user.setUserId(UUID.randomUUID().toString());
                user.setUsername(row.getUsername());
                user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                user.setCreateTime(LocalDateTime.now());
            }
            user.setNickname(row.getNickname());
            user.setEmail(row.getEmail());
            user.setPhone(row.getPhone());
            Integer rowStatus = row.getStatus();
            user.setStatus(rowStatus != null ? rowStatus : 1);
            user.setUpdateTime(LocalDateTime.now());
            if (!exists) {
                sysUserMapper.insert(user);
            } else {
                sysUserMapper.updateById(user);
                updatedUserIds.add(user.getUserId());
            }
        }
        authPermissionService.evictLoginUsers(updatedUserIds);
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreateTime));
        List<UserExcelRow> rows = new ArrayList<>();
        for (SysUser user : users) {
            UserExcelRow row = new UserExcelRow();
            row.setUsername(user.getUsername());
            row.setNickname(user.getNickname());
            row.setEmail(user.getEmail());
            row.setPhone(user.getPhone());
            row.setStatus(user.getStatus());
            rows.add(row);
        }

        try {
            String fileName = URLEncoder.encode("用户列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), UserExcelRow.class).sheet("用户").doWrite(rows);
        } catch (IOException e) {
            throw new BizException("导出失败: " + e.getMessage());
        }
    }

    private void checkUsernameUnique(String username, String excludeId) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user != null && (excludeId == null || !user.getUserId().equals(excludeId))) {
            throw new BizException("用户名已存在");
        }
    }

    private void saveUserRoles(String userId, List<String> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (String roleId : roleIds) {
            SysUserRole relation = new SysUserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            sysUserRoleMapper.insert(relation);
        }
    }

    private void saveUserCompanies(String userId, List<String> companyIds) {
        sysUserCompanyMapper.delete(new LambdaQueryWrapper<SysUserCompany>().eq(SysUserCompany::getUserId, userId));
        if (companyIds == null || companyIds.isEmpty()) {
            return;
        }
        for (String companyId : companyIds) {
            SysUserCompany relation = new SysUserCompany();
            relation.setUserId(userId);
            relation.setCompanyId(companyId);
            sysUserCompanyMapper.insert(relation);
        }
    }

    private void clearUserRelations(String userId) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        sysUserCompanyMapper.delete(new LambdaQueryWrapper<SysUserCompany>().eq(SysUserCompany::getUserId, userId));
    }

    private boolean passwordMatched(String rawPassword, String storedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(storedPassword)) {
            return false;
        }
        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    private boolean isBcryptHash(String password) {
        return StringUtils.hasText(password)
                && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}
