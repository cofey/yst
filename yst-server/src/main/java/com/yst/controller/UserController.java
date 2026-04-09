package com.yst.controller;

import com.yst.common.ApiResponse;
import com.yst.service.UserService;
import com.yst.vo.UserSaveRequest;
import com.yst.vo.UserUpdateRequest;
import com.yst.vo.UserVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring manages bean lifecycle for injected dependencies")
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserVO>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.list(keyword));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody @Valid UserSaveRequest request) {
        userService.create(request);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        userService.update(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping("/import")
    public ApiResponse<Void> importExcel(@RequestParam("file") MultipartFile file) {
        userService.importExcel(file);
        return ApiResponse.ok();
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        userService.exportExcel(response);
    }
}
