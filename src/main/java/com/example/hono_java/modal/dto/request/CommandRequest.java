// 命令下发请求体

package com.example.hono_java.modal.dto.request;

import lombok.Data;

@Data
public class CommandRequest {
    private String commandName;
    private String contentType;
    private Object payload;      // 可以是 JSON 对象或字符串
    private Integer timeoutSeconds; // 可选，默认 10
}
