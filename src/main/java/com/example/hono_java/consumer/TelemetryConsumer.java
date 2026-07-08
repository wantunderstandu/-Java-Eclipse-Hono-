package com.example.hono_java.consumer;


import com.example.hono_java.mapper.DeviceTelemetryMapper;
import com.example.hono_java.modal.dto.DeviceTelemetry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryConsumer {

    private final MqttClient mqttClient;
    private final DeviceTelemetryMapper telemetryMapper;

    @PostConstruct
    public void init() throws Exception {
        mqttClient.subscribe("telemetry/+", (topic, message) -> {
            String deviceId = topic.replace("telemetry/", "");
            String payload = new String(message.getPayload());

            DeviceTelemetry entity = new DeviceTelemetry();
            entity.setDeviceId(deviceId);
            entity.setPayload(payload);
            entity.setCreatedAt(LocalDateTime.now());
            telemetryMapper.insert(entity);

            log.info("收到遥测: device={}, payload={}", deviceId, payload);
        });

        log.info("遥测消费者已启动，订阅: telemetry/+");
    }
}
