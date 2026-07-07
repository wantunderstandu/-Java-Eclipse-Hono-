package com.example.hono_java.modal.dto.response;

import lombok.Data;

@Data
public class CommandResultData {
    private String deviceId;
    private String commandName;
    private String status;
}
