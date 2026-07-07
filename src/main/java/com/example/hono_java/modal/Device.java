package com.example.hono_java.modal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;


//映射device数据
@Data
@TableName(value="device",autoResultMap = true)
public class Device {
    //    唯一键id
    @TableId(type = IdType.AUTO)
    private Long id;

    //    deviceId 是设备唯一标识。
    @TableField("device_id")
    private String deviceId;

    //    默认
    @TableField("tenant_id")
    private String tenantId;

    //   设备名称
    @TableField("name")
    private String name;

    //    对设备的描述
    @TableField("description")
    private String description;

    //是否启用
    @TableField("enabled")
    private Boolean enabled;

    //    创建时间，默认为现在的时间
    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

}
