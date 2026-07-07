package com.example.hono_java.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hono_java.mapper.DeviceMapper;
import com.example.hono_java.modal.Device;
import com.example.hono_java.service.DeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class DeviceServiceImpl implements DeviceService {

    @Value("${hono.config_tenant_id}")
    private String tenantId;

    private final DeviceMapper deviceMapper;

    public DeviceServiceImpl(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    //添加设备逻辑
    @Override
    @Transactional
    public Device registerDevice(Device device) {
        // 强制设置租户（不信任前端传入）
        device.setTenantId(tenantId);
        // 若前端未传 enabled，默认 true
        if (device.getEnabled() == null) {
            device.setEnabled(true);
        }
        // 设置创建/更新时间（由数据库自动填充，也可以手动设置）
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());

        // 插入数据库（如果deviceId在租户内重复，会抛出DuplicateKeyException，由全局异常处理）
        deviceMapper.insert(device);
        return device;
    }

    //查询单个设备信息逻辑
    @Override
    public Device getByDeviceId(String deviceId) {
        QueryWrapper<Device> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", tenantId)
                .eq("device_id", deviceId);
        return deviceMapper.selectOne(wrapper);
    }

    //查询设备列表逻辑
    @Override
    public IPage<Device> listDevices(int pageNum, int pageSize) {
        Page<Device> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Device> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", tenantId)
                .orderByDesc("created_at"); // 按创建时间倒序
        return deviceMapper.selectPage(page, wrapper);
    }

}
