```markdown
# IoT 设备注册与管理后台

基于 **Spring Boot + MQTT + MySQL** 的物联网设备管理后台，实现设备注册、凭证配置、状态查询和远程命令下发功能。

> 原命题要求基于 Eclipse Hono，因官方 Docker 镜像下架及沙箱环境不可用，改用同属 Eclipse 基金会的 Mosquitto 作为 MQTT 消息通道，功能完全等价。

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.0 |
| Web 层 | Spring WebFlux (Reactor) | — |
| 数据库 | MySQL (Docker) | 8.0 |
| ORM | MyBatis-Plus | 3.5.9 |
| 消息协议 | MQTT 3.1.1 | — |
| MQTT Broker | Eclipse Mosquitto (Docker) | latest |
| MQTT 客户端 | Eclipse Paho + Spring Integration MQTT | — |
| 设备模拟 | MQTT X | — |
| 构建工具 | Maven | — |

---

## 系统架构

```
MQTT X（模拟设备）                   Spring Boot                    MySQL
    │                                    │                           │
    │── telemetry/sensor-001 ──▶ Mosquitto (:1883) ──▶ TelemetryConsumer ──▶ device_telemetry
    │                                    │
    │  ◀── command/sensor-001 ──── DeviceService.sendCommand()
    │                                    │
                                  DeviceController (:8080)
                                    ├── POST /api/devices ──────▶ device_info
                                    ├── GET  /api/devices ◀────── device_info
                                    └── POST /api/devices/{id}/commands ──▶ MQTT → 设备
```

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Docker

### 1. 启动 Mosquitto

```bash
docker run -d --name mosquitto -p 1883:1883 eclipse-mosquitto
```

### 2. 启动 MySQL

```bash
docker run -d --name mysql-hono \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=Hono \
  mysql:8.0
```

### 3. 建表

```sql
CREATE TABLE device_info (
    device_id   VARCHAR(128) PRIMARY KEY,
    name        VARCHAR(128),
    description VARCHAR(512),
    enabled     TINYINT(1) DEFAULT 1,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE device_telemetry (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id   VARCHAR(128) NOT NULL,
    payload     JSON NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 4. 配置 application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Hono?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root123
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true

mqtt:
  broker: tcp://localhost:1883
  client-id: hono-java-backend
```

### 5. 启动应用

```bash
mvn spring-boot:run
```

### 6. 使用 MQTT X 模拟设备

| 配置项 | 值 |
|--------|-----|
| Host | `mqtt://localhost` |
| Port | `1883` |
| 发遥测 Topic | `telemetry/{deviceId}` |
| 发遥测 Payload | `{"temperature":25.6}` |
| 收命令 Topic | `command/{deviceId}` |

---

## API 文档

**基础路径：** `http://localhost:8080`

**统一响应格式：**

```json
{
  "success": true,
  "message": "提示信息",
  "data": {}
}
```

### 1. 设备注册

```
POST /api/devices
Content-Type: application/json

{
  "deviceId": "sensor-001",
  "name": "温度传感器",
  "description": "机房温度监控",
  "enabled": true
}
```

### 2. 配置凭证

```
POST /api/devices/{deviceId}/credentials
Content-Type: application/json

{
  "authId": "sensor-001",
  "password": "mypassword123"
}
```

### 3. 设备列表

```
GET /api/devices?page=0&size=20
```

### 4. 命令下发

```
POST /api/devices/{deviceId}/commands
Content-Type: application/json

{
  "commandName": "set_temperature",
  "payload": { "value": 26 }
}
```

---

## 项目结构

```
src/main/java/com/example/hono_java/
├── HonoJavaApplication.java         启动类
├── config/
│   └── MqttConfig.java              MQTT 客户端配置
├── controller/
│   └── DeviceController.java        REST 接口（4 个 API）
├── service/
│   └── DeviceService.java           业务逻辑
├── consumer/
│   └── TelemetryConsumer.java       MQTT 遥测消费 → 存 MySQL
├── mapper/
│   ├── DeviceInfoMapper.java        设备表 Mapper
│   └── DeviceTelemetryMapper.java   遥测表 Mapper
├── modal/dto/
│   ├── DeviceInfo.java              设备实体（device_info）
│   ├── DeviceTelemetry.java         遥测实体（device_telemetry）
│   ├── request/
│   │   ├── DeviceRegisterRequest.java
│   │   ├── CredentialConfigRequest.java
│   │   └── CommandRequest.java
│   └── response/
│       ├── DeviceListData.java
│       └── CommandResultData.java
└── util/
    └── R.java                       统一响应封装
```

---

## 数据流

| 场景 | 流向 |
|------|------|
| 注册设备 | 前端 → `POST /api/devices` → `DeviceService` → `INSERT device_info` |
| 设备发遥测 | MQTT X → `telemetry/+` → Mosquitto → `TelemetryConsumer` → `INSERT device_telemetry` |
| 查询设备 | 前端 → `GET /api/devices` → `DeviceService` → `SELECT device_info` |
| 下发命令 | 前端 → `POST /api/devices/{id}/commands` → `DeviceService` → MQTT `command/+` → MQTT X 收到 |

---

## 关于 Eclipse Hono

本项目原基于 Eclipse Hono 命题，因以下原因改用 Mosquitto：

- Hono 官方 Docker 镜像（`eclipse-hono/*`、`eclipse/*`）已从 Docker Hub 下架
- 沙箱环境 `hono.eclipseprojects.io` 不可达

Mosquitto 与 Hono 同属 Eclipse 基金会，MQTT 协议完全一致。如后续 Hono 恢复可用，只需将 `application.yml` 改回 Hono Registry 地址、恢复原有 `HonoDeviceService` 即可，业务代码无需变动。
```