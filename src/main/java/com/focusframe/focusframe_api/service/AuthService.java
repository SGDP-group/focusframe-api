package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.controller.AuthController;
import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.repository.AuthRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthService (AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public AuthToken GenerateAndSaveAuth (String ipAddress) {
        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(ipAddress, token);

        return authRepository.save(authToken);
    }

    public Boolean AuthenticateToken (String userToken) {
        try {
            Optional<AuthToken> token =  authRepository.findByToken(userToken);

            if ((token.isEmpty()) ||(token.get().getExpired())) {
                return false;
            }
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
        authRepository.setAuthAsExpired(userToken);
        return true;
    }
}
