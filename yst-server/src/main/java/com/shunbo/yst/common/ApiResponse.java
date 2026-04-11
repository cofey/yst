package com.shunbo.yst.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(200, "success", null);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(500, message, null);
    }

    public static ApiResponse<Void> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }
}
