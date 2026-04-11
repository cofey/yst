package com.shunbo.yst.modules.system.user.vo;

import com.shunbo.yst.common.annotation.DateTimePattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private String userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private List<String> companyIds;
    private List<String> companyNames;
    private List<String> roleIds;
    private List<String> roleNames;
    private Integer status;
    @DateTimePattern
    private LocalDateTime createTime;
}
