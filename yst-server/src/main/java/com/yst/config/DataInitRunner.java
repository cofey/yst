package com.yst.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yst.entity.SysUser;
import com.yst.mapper.SysUserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitRunner implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitRunner(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        SysUser admin = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, "admin")
                .last("limit 1"));
        if (admin != null) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername("admin");
        user.setNickname("超级管理员");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEmail("admin@yst.com");
        user.setPhone("13800000000");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
    }
}
