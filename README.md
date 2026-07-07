# Java Eclipse Hono 设备管理后台 MVP

本项目是“基于 Java + Eclipse Hono 的物联网设备注册与管理后台开发”的最小 MVP 实现。后端使用 Spring Boot 封装管理接口，并通过 WebClient 调用 Eclipse Hono Device Registry Management API，实现设备注册、凭证配置、设备列表查询和命令下发请求提交。

## 功能范围

- 设备注册：将设备写入 Hono Device Registry，并保存 `name`、`description` 等扩展字段。
- 设备凭证配置：调用 Hono Credentials Management API，为设备配置连接凭证。
- 设备列表查询：从 Hono Device Registry 查询当前租户设备列表。
- 命令下发：MVP 阶段先校验设备存在并返回 `SENT`，不强制等待设备响应。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring WebFlux / WebClient
- Eclipse Hono Device Registry
- Docker Compose
- Maven Wrapper

## 项目结构

```text
.
├── docker-compose.yml
├── pom.xml
├── 需求接口文档.md
└── src
    ├── main
    │   ├── java/com/example/hono_java
    │   │   ├── config
    │   │   ├── controller
    │   │   ├── modal/dto
    │   │   ├── service
    │   │   └── util
    │   └── resources
    └── test
```

## 本地运行

### 1. 启动 Hono 相关服务

```powershell
docker compose up -d
```

默认会启动：

- Hono Device Registry：`http://localhost:28080`
- Hono HTTP Adapter：`http://localhost:8080`
- AMQP 端口：`5672`

### 2. 启动 Spring Boot 后端

```powershell
.\mvnw.cmd spring-boot:run
```

后端默认端口：

```text
http://localhost:7780
```

### 3. 运行测试

```powershell
.\mvnw.cmd test
```

## 配置说明

核心配置位于 `src/main/resources/application.yml`：

```yaml
hono:
  tenant-id: DEFAULT_TENANT
  registry:
    url: http://localhost:28080
```

`tenant-id` 由后端固定配置，前端不需要也不应该传入 `tenantId`。

## API 示例

### 设备注册

```powershell
curl.exe -X POST http://localhost:7780/api/devices `
  -H "Content-Type: application/json" `
  -d '{"deviceId":"device-001","name":"温湿度传感器-001","description":"实验室一号传感器","enabled":true}'
```

成功响应：

```json
{
  "success": true,
  "message": "设备注册成功",
  "data": {
    "deviceId": "device-001",
    "tenantId": "DEFAULT_TENANT",
    "enabled": true
  }
}
```

### 设备凭证配置

```powershell
curl.exe -X POST http://localhost:7780/api/devices/device-001/credentials `
  -H "Content-Type: application/json" `
  -d '{"authId":"device-001","password":"123456"}'
```

### 设备列表查询

```powershell
curl.exe "http://localhost:7780/api/devices?page=0&size=20"
```

### 命令下发

```powershell
curl.exe -X POST http://localhost:7780/api/devices/device-001/commands `
  -H "Content-Type: application/json" `
  -d '{"commandName":"setTemperature","contentType":"application/json","payload":{"targetTemperature":26},"timeoutSeconds":10}'
```

响应示例：

```json
{
  "success": true,
  "message": "命令下发请求已提交",
  "data": {
    "deviceId": "device-001",
    "commandName": "setTemperature",
    "status": "SENT"
  }
}
```

## MVP 边界

当前命令下发接口完成的是“管理后台提交命令请求”的最小闭环：后端会先检查设备是否存在，然后返回 `SENT`。它还没有真正接入 Hono 的 AMQP/Kafka Command & Control 通道，也不会等待设备响应。

如果后续需要演示真实设备接收命令，需要继续补充业务应用侧的 Hono Command & Control 消费/发送链路。

## 参考文档

- Eclipse Hono 文档：https://eclipse.dev/hono/docs/
- Device Registry Management API：https://eclipse.dev/hono/docs/api/management/
