package com.shunbo.yst.modules.system.user.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.auth.vo.LoginRequest;
import com.shunbo.yst.modules.auth.vo.LoginResponse;
import com.shunbo.yst.modules.auth.vo.AuthInfoVO;
import com.shunbo.yst.modules.system.user.vo.UserQueryRequest;
import com.shunbo.yst.modules.system.user.vo.UserSaveRequest;
import com.shunbo.yst.modules.system.user.vo.UserUpdateRequest;
import com.shunbo.yst.modules.system.user.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    LoginResponse login(LoginRequest request);

    AuthInfoVO currentAuthInfo();

    PageResult<UserVO> list(UserQueryRequest query);

    void create(UserSaveRequest request);

    void update(String userId, UserUpdateRequest request);

    void delete(String userId);

    void importExcel(MultipartFile file);

    void exportExcel(HttpServletResponse response);
}
