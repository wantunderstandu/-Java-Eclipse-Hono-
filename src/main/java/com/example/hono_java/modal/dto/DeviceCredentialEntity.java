package com.example.hono_java.modal.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("iot_device_credential")
public class DeviceCredentialEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "")
    private String tenantId;

    @TableField(value = "")
    private String deviceId;

    @TableField(value = "")
    private String authId;

    @TableField(value = "credential_type")
    private String CredentialType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
