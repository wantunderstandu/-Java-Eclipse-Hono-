package com.example.hono_java.modal.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("iot_device")
public class DeviceEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "device_id")
    private String deviceId;

    private String name;

    private String description;

    private Boolean enabled;

    @TableField(value = "created_at")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;
}