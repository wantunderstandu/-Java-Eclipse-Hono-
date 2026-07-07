package com.example.hono_java.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hono_java.modal.Device;

public interface DeviceService {
    /**
     * 设备注册
     * @param device 设备实体（tenantId由后端自动填充，调用方不需传入）
     * @return 注册成功的设备信息（含数据库生成的id等）
     */
    Device registerDevice(Device device);

    /**
     * 根据deviceId查询设备
     */
    Device getByDeviceId(String deviceId);

    /**
     * 分页查询当前租户下的设备列表
     * @param pageNum  页码（从0开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    IPage<Device> listDevices(int pageNum, int pageSize);

}
