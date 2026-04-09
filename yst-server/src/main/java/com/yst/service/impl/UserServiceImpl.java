package com.yst.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yst.common.BizException;
import com.yst.entity.SysUser;
import com.yst.mapper.SysUserMapper;
import com.yst.security.JwtTokenUtil;
import com.yst.service.UserService;
import com.yst.vo.LoginRequest;
import com.yst.vo.LoginResponse;
import com.yst.vo.UserExcelRow;
import com.yst.vo.UserSaveRequest;
import com.yst.vo.UserUpdateRequest;
import com.yst.vo.UserVO;
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
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public UserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BizException("用户已被禁用");
        }
        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername());
    }

    @Override
    public List<UserVO> list(String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        List<UserVO> result = new ArrayList<>();
        for (SysUser user : users) {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public void create(UserSaveRequest request) {
        checkUsernameUnique(request.getUsername(), null);
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        Integer requestStatus = request.getStatus();
        user.setStatus(requestStatus != null ? requestStatus : Integer.valueOf(1));
        String rawPassword = StringUtils.hasText(request.getPassword()) ? request.getPassword() : DEFAULT_PASSWORD;
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
    }

    @Override
    public void update(Long id, UserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(id);
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
    }

    @Override
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        sysUserMapper.deleteById(id);
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

        for (UserExcelRow row : rows) {
            if (!StringUtils.hasText(row.getUsername()) || !StringUtils.hasText(row.getNickname())) {
                continue;
            }
            SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, row.getUsername()));
            if (user == null) {
                user = new SysUser();
                user.setUsername(row.getUsername());
                user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                user.setCreateTime(LocalDateTime.now());
            }
            user.setNickname(row.getNickname());
            user.setEmail(row.getEmail());
            user.setPhone(row.getPhone());
            Integer rowStatus = row.getStatus();
            user.setStatus(rowStatus != null ? rowStatus : Integer.valueOf(1));
            user.setUpdateTime(LocalDateTime.now());
            if (user.getId() == null) {
                sysUserMapper.insert(user);
            } else {
                sysUserMapper.updateById(user);
            }
        }
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().orderByDesc(SysUser::getCreateTime));
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

    private void checkUsernameUnique(String username, Long excludeId) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user != null && (excludeId == null || !user.getId().equals(excludeId))) {
            throw new BizException("用户名已存在");
        }
    }
}
