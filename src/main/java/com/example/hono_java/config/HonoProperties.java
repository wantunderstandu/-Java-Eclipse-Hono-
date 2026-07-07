package com.example.hono_java.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hono")
public class HonoProperties {
    private String tenantId = "DEFAULT_TENANT";
    private Registry registry = new Registry();

    @Data
    public static class Registry {
        private String url = "http://localhost:28080";
    }
}
