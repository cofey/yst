package com.shunbo.yst.modules.system.service;

import com.shunbo.yst.common.PageResult;
import com.shunbo.yst.modules.auth.vo.LoginRequest;
import com.shunbo.yst.modules.auth.vo.LoginResponse;
import com.shunbo.yst.modules.auth.vo.AuthInfoVO;
import com.shunbo.yst.modules.system.vo.UserQueryRequest;
import com.shunbo.yst.modules.system.vo.UserSaveRequest;
import com.shunbo.yst.modules.system.vo.UserUpdateRequest;
import com.shunbo.yst.modules.system.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * UserService 服务接口，定义业务能力与契约。
 */
public interface UserService {

    /**
     * 执行login相关处理。
     */

    LoginResponse login(LoginRequest request);

    /**
     * 执行currentAuthInfo相关处理。
     */

    AuthInfoVO currentAuthInfo();

    /**
     * 执行list相关处理。
     */

    PageResult<UserVO> list(UserQueryRequest query);

    /**
     * 执行create相关处理。
     */

    void create(UserSaveRequest request);

    /**
     * 执行update相关处理。
     */

    void update(String userId, UserUpdateRequest request);

    /**
     * 执行delete相关处理。
     */

    void delete(String userId);

    /**
     * 执行importExcel相关处理。
     */

    void importExcel(MultipartFile file);

    /**
     * 执行exportExcel相关处理。
     */

    void exportExcel(HttpServletResponse response);
}
