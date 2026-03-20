package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.repository.AuthRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthService {
    private final AuthRepository authRepository;

    public AuthService (AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public AuthToken GenerateAndSaveAuth (String ipAddress) {
        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(ipAddress, token);

        return authRepository.save(authToken);
    }
}
