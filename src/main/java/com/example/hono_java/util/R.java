package com.example.hono_java.util;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class R<T> {
    private boolean success;
    private String message;
    private T data;

    private R(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // ---------- 静态工厂方法 ----------
    public static <T> R<T> success(String message, T data) {
        return new R<>(true, message, data);
    }

    public static <T> R<T> success(T data) {
        return success("成功", data);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(false, message, null);
    }

    public static R<String> success() {
        return success("成功");
    }
}
