package com.example.hono_java.controller;


import com.example.hono_java.modal.dto.request.CommandRequest;
import com.example.hono_java.modal.dto.request.CredentialConfigRequest;
import com.example.hono_java.modal.dto.request.DeviceRegisterRequest;
import com.example.hono_java.modal.dto.response.CommandResultData;
import com.example.hono_java.modal.dto.response.DeviceListData;
import com.example.hono_java.service.HonoDeviceService;
import com.example.hono_java.util.R;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/devices")
@Validated
public class DeviceController {

    private final HonoDeviceService honoDeviceService;

    public DeviceController(HonoDeviceService honoDeviceService) {
        this.honoDeviceService = honoDeviceService;
    }

    // 1. 设备注册
    @PostMapping
    public Mono<ResponseEntity<R<Object>>> registerDevice(
            @RequestBody DeviceRegisterRequest request
    ) {
        return honoDeviceService.registerDevice(request)
                .map(data -> ResponseEntity.ok(R.success("设备注册成功", (Object) data)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(R.fail(e.getMessage()))));
    }

    // 2. 设备凭证配置
    @PostMapping("/{deviceId}/credentials")
    public Mono<ResponseEntity<R<Object>>> configureCredentials(
            @PathVariable String deviceId,
            @RequestBody CredentialConfigRequest request
    ) {
        return honoDeviceService.configureCredentials(deviceId, request)
                .map(data -> ResponseEntity.ok(R.success("设备凭证配置成功", (Object) data)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(R.fail(e.getMessage()))));
    }

    // 3. 设备列表查询
    @GetMapping
    public Mono<ResponseEntity<R<DeviceListData>>> listDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return honoDeviceService.listDevices(page, size)
                .map(data -> ResponseEntity.ok(R.success("查询成功", data)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(R.fail(e.getMessage()))));
    }

    // 4. 命令下发
    @PostMapping("/{deviceId}/commands")
    public Mono<ResponseEntity<R<CommandResultData>>> sendCommand(
            @PathVariable String deviceId,
            @RequestBody CommandRequest request
    ) {
        return honoDeviceService.sendCommand(deviceId, request)
                .map(data -> ResponseEntity.ok(R.success("命令下发请求已提交", data)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(R.fail(e.getMessage()))));
    }
}
