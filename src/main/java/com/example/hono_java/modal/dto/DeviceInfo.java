
package com.example.hono_java.modal.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("device_info")
public class DeviceInfo {
    @TableId
    private String deviceId;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
