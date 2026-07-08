CREATE TABLE IF NOT EXISTS device_info (
    device_id   VARCHAR(128) PRIMARY KEY,
    name        VARCHAR(128),
    description VARCHAR(512),
    enabled     TINYINT(1) DEFAULT 1,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS device_telemetry (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id   VARCHAR(128) NOT NULL,
    payload     JSON NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);
