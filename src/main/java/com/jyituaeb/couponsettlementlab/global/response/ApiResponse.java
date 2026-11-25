package com.jyituaeb.couponsettlementlab.global.response;

public record ApiResponse<T>(boolean success, T data, String errorCode) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(String errorCode) {
        return new ApiResponse<>(false, null, errorCode);
    }
}
