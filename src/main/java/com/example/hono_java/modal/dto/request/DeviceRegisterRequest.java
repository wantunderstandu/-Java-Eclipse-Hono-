// 设备注册请求体

package com.example.hono_java.modal.dto.request;


import lombok.Data;

@Data
public class DeviceRegisterRequest {
    private String deviceId;
    private String name;
    private String description;
    private Boolean enabled;  // 可选，默认为 true
}
