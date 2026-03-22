package com.focusframe.focusframe_api.controller;

import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/authToken")
public class AuthController {
    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AuthToken> generateToken(@RequestBody String ip)
    {
        log.info("AuthController|generateToken|Initialized");
        try {
            AuthToken authToken = authService.GenerateAndSaveAuth(ip);
            log.info("AuthController|Success|Response:" + authToken);
            return ResponseEntity.ok(authToken);
        } catch (Exception e) {
            log.info("AuthController|Failure|" + e);
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/authenticate")
    public Boolean authenticateToken (@RequestBody Map<String, String> payload) {

        log.info("AuthController|authenticateToken|Initialized");
        try {
            String token = payload.get("token");
            if (token == null || token.isBlank()) {
                log.error("AuthController|authenticateToken|missing token in request body");
                return false;
            }
            return authService.AuthenticateToken(token);
        } catch (Exception e) {
            log.error("AuthController|authenticateToken|failure|" + e);
            return false;
        }
    }
}
