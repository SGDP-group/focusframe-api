package com.focusframe.focusframe_api.controller;

import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        log.info("AuthController|Initialized");
        try {
            AuthToken authToken = authService.GenerateAndSaveAuth(ip);
            log.info("AuthController|Success|Response:" + authToken);
            return ResponseEntity.ok(authToken);
        } catch (Exception e) {
            log.info("AuthController|Failure|" + e);
            throw new RuntimeException(e);
        }
    }
}
