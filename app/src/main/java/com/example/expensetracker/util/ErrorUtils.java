package com.example.expensetracker.util;

import java.io.IOException;
import retrofit2.HttpException;

public class ErrorUtils {
    public static String getErrorMessage(Throwable throwable, String defaultMessage) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int code = httpException.code();
            
            if (code == 429) {
                try {
                    String retryAfter = httpException.response().headers().get("Retry-After");
                    if (retryAfter != null && !retryAfter.trim().isEmpty()) {
                        return "Thao tác quá nhanh, vui lòng chờ " + retryAfter + " giây.";
                    }
                } catch (Exception ignored) {
                }
                return "Thao tác quá nhanh, vui lòng thử lại sau.";
            }
            if (code == 500 || code == 502 || code == 503 || code == 504) {
                return "Lỗi hệ thống máy chủ (" + code + "). Vui lòng thử lại sau.";
            }
            if (code == 404) {
                return "Không tìm thấy dữ liệu yêu cầu.";
            }
            if (code == 403) {
                return "Bạn không có quyền truy cập dữ liệu này.";
            }
            if (code == 408) {
                return "Yêu cầu hết thời gian chờ.";
            }
            if (code == 422) {
                return "Dữ liệu gửi đi không hợp lệ.";
            }
        }
        
        if (throwable instanceof IOException) {
            return "Không thể kết nối đến máy chủ. Vui lòng kiểm tra mạng.";
        }
        
        return defaultMessage;
    }
}
