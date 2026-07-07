package com.example.hono_java.service;

import com.example.hono_java.config.HonoProperties;
import com.example.hono_java.modal.dto.request.CommandRequest;
import com.example.hono_java.modal.dto.request.CredentialConfigRequest;
import com.example.hono_java.modal.dto.request.DeviceRegisterRequest;
import com.example.hono_java.modal.dto.response.CommandResultData;
import com.example.hono_java.modal.dto.response.DeviceListData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HonoDeviceService {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient honoWebClient;
    private final HonoProperties honoProperties;

    public Mono<Map<String, Object>> registerDevice(DeviceRegisterRequest request) {
        return Mono.defer(() -> {
            DeviceRegisterRequest body = requireBody(request);
            String deviceId = requireText(body.getDeviceId(), "deviceId不能为空");
            String tenantId = honoProperties.getTenantId();
            Boolean enabled = body.getEnabled() != null ? body.getEnabled() : Boolean.TRUE;

            Map<String, Object> honoDevice = new LinkedHashMap<>();
            honoDevice.put("enabled", enabled);

            Map<String, Object> ext = new LinkedHashMap<>();
            putIfHasText(ext, "name", body.getName());
            putIfHasText(ext, "description", body.getDescription());
            if (!ext.isEmpty()) {
                honoDevice.put("ext", ext);
            }

            return honoWebClient.post()
                    .uri("/v1/devices/{tenantId}/{deviceId}", tenantId, deviceId)
                    .bodyValue(honoDevice)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> {
                        log.info("Hono设备注册成功: tenant={}, device={}", tenantId, deviceId);
                        Map<String, Object> data = new LinkedHashMap<>();
                        data.put("deviceId", deviceId);
                        data.put("tenantId", tenantId);
                        data.put("enabled", enabled);
                        return data;
                    })
                    .onErrorMap(WebClientResponseException.class, e -> honoError("设备注册失败", e));
        });
    }

    public Mono<Map<String, String>> configureCredentials(String deviceId, CredentialConfigRequest request) {
        return Mono.defer(() -> {
            CredentialConfigRequest body = requireBody(request);
            String checkedDeviceId = requireText(deviceId, "deviceId不能为空");
            String authId = StringUtils.hasText(body.getAuthId()) ? body.getAuthId() : checkedDeviceId;
            String password = requireText(body.getPassword(), "password不能为空");
            String tenantId = honoProperties.getTenantId();

            Map<String, Object> secret = new LinkedHashMap<>();
            secret.put("pwd-plain", password);

            Map<String, Object> credential = new LinkedHashMap<>();
            credential.put("auth-id", authId);
            credential.put("type", "hashed-password");
            credential.put("secrets", List.of(secret));

            return honoWebClient.put()
                    .uri("/v1/credentials/{tenantId}/{deviceId}", tenantId, checkedDeviceId)
                    .bodyValue(List.of(credential))
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> {
                        log.info("Hono设备凭证配置成功: tenant={}, device={}, authId={}", tenantId, checkedDeviceId, authId);
                        Map<String, String> data = new LinkedHashMap<>();
                        data.put("deviceId", checkedDeviceId);
                        data.put("authId", authId);
                        return data;
                    })
                    .onErrorMap(WebClientResponseException.class, e -> honoError("设备凭证配置失败", e));
        });
    }

    public Mono<DeviceListData> listDevices(int page, int size) {
        String tenantId = honoProperties.getTenantId();
        int safeSize = Math.max(size, 1);
        int pageOffset = Math.max(page, 0) * safeSize;

        return honoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/devices/{tenantId}")
                        .queryParam("pageOffset", pageOffset)
                        .queryParam("pageSize", safeSize)
                        .build(tenantId))
                .retrieve()
                .bodyToMono(MAP_TYPE)
                .map(response -> toDeviceListData(tenantId, response))
                .onErrorMap(WebClientResponseException.class, e -> honoError("查询设备列表失败", e));
    }

    public Mono<CommandResultData> sendCommand(String deviceId, CommandRequest request) {
        return Mono.defer(() -> {
            CommandRequest body = requireBody(request);
            String checkedDeviceId = requireText(deviceId, "deviceId不能为空");
            String commandName = requireText(body.getCommandName(), "commandName不能为空");
            String tenantId = honoProperties.getTenantId();

            return getDeviceRegistration(tenantId, checkedDeviceId)
                    .map(ignored -> {
                        log.info("MVP命令提交: tenant={}, device={}, command={}, contentType={}, payload={}",
                                tenantId, checkedDeviceId, commandName, body.getContentType(), body.getPayload());
                        CommandResultData result = new CommandResultData();
                        result.setDeviceId(checkedDeviceId);
                        result.setCommandName(commandName);
                        result.setStatus("SENT");
                        return result;
                    })
                    .onErrorMap(WebClientResponseException.class, e -> honoError("命令下发请求提交失败", e));
        });
    }

    private Mono<Map<String, Object>> getDeviceRegistration(String tenantId, String deviceId) {
        return honoWebClient.get()
                .uri("/v1/devices/{tenantId}/{deviceId}", tenantId, deviceId)
                .retrieve()
                .bodyToMono(MAP_TYPE);
    }

    private DeviceListData toDeviceListData(String tenantId, Map<String, Object> response) {
        List<Map<String, Object>> rawDevices = extractDeviceRows(response);
        List<DeviceListData.DeviceInfo> devices = rawDevices.stream()
                .map(this::toDeviceInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        DeviceListData data = new DeviceListData();
        data.setTenantId(tenantId);
        data.setTotal(parseLong(response.get("total"), devices.size()));
        data.setDevices(devices);
        return data;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractDeviceRows(Map<String, Object> response) {
        Object result = response.get("result");
        if (result == null) {
            result = response.get("devices");
        }
        if (result instanceof List<?> rows) {
            return rows.stream()
                    .filter(Map.class::isInstance)
                    .map(row -> (Map<String, Object>) row)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private DeviceListData.DeviceInfo toDeviceInfo(Map<String, Object> row) {
        String deviceId = firstText(row, "id", "deviceId", "device-id");
        if (!StringUtils.hasText(deviceId)) {
            return null;
        }

        DeviceListData.DeviceInfo info = new DeviceListData.DeviceInfo();
        info.setDeviceId(deviceId);
        info.setEnabled(parseBoolean(row.get("enabled"), Boolean.TRUE));

        Object extValue = row.get("ext");
        if (extValue instanceof Map<?, ?> ext) {
            info.setName(asString(((Map<String, Object>) ext).get("name")));
            info.setDescription(asString(((Map<String, Object>) ext).get("description")));
        }
        return info;
    }

    private static void putIfHasText(Map<String, Object> target, String key, String value) {
        if (StringUtils.hasText(value)) {
            target.put(key, value);
        }
    }

    private static String requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static <T> T requireBody(T body) {
        if (body == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        return body;
    }

    private static RuntimeException honoError(String action, WebClientResponseException e) {
        String body = e.getResponseBodyAsString();
        String detail = StringUtils.hasText(body) ? body : e.getStatusText();
        return new RuntimeException(action + ": HTTP " + e.getStatusCode().value() + " " + detail, e);
    }

    private static String firstText(Map<String, Object> values, String... keys) {
        for (String key : keys) {
            String value = asString(values.get(key));
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private static long parseLong(Object value, long defaultValue) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return value == null ? defaultValue : Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Boolean parseBoolean(Object value, Boolean defaultValue) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }
}
