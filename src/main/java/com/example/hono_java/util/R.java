package com.example.hono_java.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Getter
// 私有化全参构造，强制只能通过静态方法创建对象（更规范）
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Data
public class R<T> {
    private final boolean succeed;
    private final T data;
    private final String message;

    // ---------- 静态工厂方法 ----------
    public static <T> R<T> success(T data) {
        return new R<>(true, data, null);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(false, null, message);
    }

    public static R<String> success() {
        return success("成功");
    }


}
