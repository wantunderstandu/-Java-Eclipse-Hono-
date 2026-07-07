package com.example.hono_java.modal;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


//映射device_credentials
@Data
@TableName(value = "device_credentials", autoResultMap = true)
public class DeviceCredentials {

//    唯一键id
    @TableId(type = IdType.AUTO)
    private Long id;

//    deviceId 是设备唯一标识。
    @TableField("device_id")
    private String deviceId;

//    authId 是设备认证 ID，MVP 中默认可以和 deviceId 保持一致。
    @TableField("auth_id")
    private String authId;

//    默认为password
    @TableField("credential_type")
    private String credentialType="password";

//    创建时间，默认为现在的时间
    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
