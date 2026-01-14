package com.viandasApp.api.Auth.dto;
import lombok.Data;

@Data
public class PasswordResetChangeDTO {
    private String token;
    private String newPassword;
}
