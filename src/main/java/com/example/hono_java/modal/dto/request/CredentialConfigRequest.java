// 凭证配置请求体

package com.example.hono_java.modal.dto.request;


import lombok.Data;

@Data
public class CredentialConfigRequest {
    private String authId;
    private String password;
}
