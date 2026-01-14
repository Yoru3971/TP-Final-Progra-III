package com.viandasApp.api.Auth.service;

import com.viandasApp.api.Auth.dto.PasswordResetChangeDTO;

public interface PasswordResetService {
    void createResetTokenForUser(String email);
    void changeUserPassword(PasswordResetChangeDTO passwordDto);
}
