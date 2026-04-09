package com.yst.service;

import com.yst.vo.LoginRequest;
import com.yst.vo.LoginResponse;
import com.yst.vo.UserSaveRequest;
import com.yst.vo.UserUpdateRequest;
import com.yst.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    LoginResponse login(LoginRequest request);

    List<UserVO> list(String keyword);

    void create(UserSaveRequest request);

    void update(Long id, UserUpdateRequest request);

    void delete(Long id);

    void importExcel(MultipartFile file);

    void exportExcel(HttpServletResponse response);
}
