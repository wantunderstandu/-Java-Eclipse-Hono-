package com.example.hono_java.modal.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DeviceListData {
    private String tenantId;
    private long total;
    private List<DeviceInfo> devices;

    @Data
    public static class DeviceInfo {
        private String deviceId;
        private String name;
        private String description;
        private Boolean enabled;
    }
}
