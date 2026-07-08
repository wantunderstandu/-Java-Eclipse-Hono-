package com.example.hono_java.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.hono_java.mapper.DeviceInfoMapper;
import com.example.hono_java.modal.dto.DeviceInfo;
import com.example.hono_java.modal.dto.request.CommandRequest;
import com.example.hono_java.modal.dto.request.CredentialConfigRequest;
import com.example.hono_java.modal.dto.request.DeviceRegisterRequest;
import com.example.hono_java.modal.dto.response.CommandResultData;
import com.example.hono_java.modal.dto.response.DeviceListData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceInfoMapper deviceInfoMapper;
    private final MqttClient mqttClient;

    // ========== 1. 设备注册 ==========
    public Mono<DeviceInfo> registerDevice(DeviceRegisterRequest request) {
        return Mono.fromCallable(() -> {
            DeviceInfo device = new DeviceInfo();
            device.setDeviceId(request.getDeviceId());
            device.setName(request.getName());
            device.setDescription(request.getDescription());
            device.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
            device.setCreatedAt(LocalDateTime.now());
            deviceInfoMapper.insert(device);
            log.info("设备注册成功: {}", request.getDeviceId());
            return device;
        });
    }

    // ========== 2. 凭证配置 ==========
    public Mono<String> configureCredentials(String deviceId, CredentialConfigRequest request) {
        return Mono.fromCallable(() -> {
            DeviceInfo device = deviceInfoMapper.selectById(deviceId);
            if (device == null) {
                throw new RuntimeException("设备不存在: " + deviceId);
            }
            log.info("设备凭证已配置: device={}, authId={}", deviceId, request.getAuthId());
            return deviceId;
        });
    }

    // ========== 3. 设备列表 ==========
    public Mono<DeviceListData> listDevices(int page, int size) {
        return Mono.fromCallable(() -> {
            Page<DeviceInfo> pageResult = deviceInfoMapper.selectPage(
                    new Page<>(page + 1, size),
                    new LambdaQueryWrapper<DeviceInfo>().orderByDesc(DeviceInfo::getCreatedAt)
            );

            List<DeviceListData.DeviceInfo> devices = pageResult.getRecords().stream()
                    .map(d -> {
                        DeviceListData.DeviceInfo info = new DeviceListData.DeviceInfo();
                        info.setDeviceId(d.getDeviceId());
                        info.setName(d.getName());
                        info.setDescription(d.getDescription());
                        info.setEnabled(d.getEnabled());
                        return info;
                    }).collect(Collectors.toList());

            DeviceListData data = new DeviceListData();
            data.setTenantId("DEFAULT_TENANT");
            data.setTotal(pageResult.getTotal());
            data.setDevices(devices);
            return data;
        });
    }

    // ========== 4. 命令下发（MQTT → 设备）==========
    public Mono<CommandResultData> sendCommand(String deviceId, CommandRequest request) {
        return Mono.fromCallable(() -> {
            DeviceInfo device = deviceInfoMapper.selectById(deviceId);
            if (device == null) {
                throw new RuntimeException("设备不存在: " + deviceId);
            }

            String topic = "command/" + deviceId;
            String payload = request.getPayload().toString();
            mqttClient.publish(topic, new MqttMessage(payload.getBytes()));

            log.info("命令已下发: device={}, command={}, topic={}", deviceId, request.getCommandName(), topic);

            CommandResultData result = new CommandResultData();
            result.setDeviceId(deviceId);
            result.setCommandName(request.getCommandName());
            result.setStatus("SENT");
            return result;
        });
    }
}
