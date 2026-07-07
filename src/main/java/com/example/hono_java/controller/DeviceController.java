package com.example.hono_java.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hono_java.modal.Device;
import com.example.hono_java.modal.dto.request.DeviceRegisterRequest;
import com.example.hono_java.service.DeviceService;
import com.example.hono_java.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@Validated
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    // 1. 设备注册
    @PostMapping
    public R<Device> registerDevice(
            @RequestBody DeviceRegisterRequest request
    ) {
        // 构建 Device 实体（tenantId 由 Service 内部设置）
        Device device = new Device();
        device.setDeviceId(request.getDeviceId());
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        try {
            Device saved = deviceService.registerDevice(device);
            return R.success(saved);
        } catch (Exception e) {
            return R.fail("设备注册失败：" + e.getMessage());
        }
    }

    // 2. 设备列表查询（分页）
    @GetMapping
    public R<IPage<Device>> listDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            IPage<Device> pageResult = deviceService.listDevices(page, size);
            return R.success(pageResult);
        } catch (Exception e) {
            return R.fail("查询设备列表失败：" + e.getMessage());
        }
    }


}
