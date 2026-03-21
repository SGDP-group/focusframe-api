package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.repository.AuthRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
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
