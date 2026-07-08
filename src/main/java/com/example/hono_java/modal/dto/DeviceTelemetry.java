package com.example.hono_java.modal.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("device_telemetry")
public class DeviceTelemetry {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceId;
    private String payload;
    private LocalDateTime createdAt;
}
