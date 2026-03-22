package com.focusframe.focusframe_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focusframe.focusframe_api.controller.AuthController;
import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.model.User;
import com.focusframe.focusframe_api.repository.AuthRepository;
import com.focusframe.focusframe_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final Executor asyncExecutor;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthService (AuthRepository authRepository,
                        UserRepository userRepository,
                        RestTemplate restTemplate,
                        @Qualifier("authAsyncExecutor") Executor asyncExecutor) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.asyncExecutor = asyncExecutor;
    }

    public AuthToken GenerateAndSaveAuth (String ipAddress, String callbackUrl) {
        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(ipAddress, callbackUrl, token);
        AuthToken savedToken = authRepository.save(authToken);
        CompletableFuture.runAsync(() -> waitForUserIdAndNotify(savedToken), asyncExecutor);
        return savedToken;
    }

    public Boolean AuthenticateToken (String userToken, String email) {
        Optional<User> user;
        try {
            Optional<AuthToken> token =  authRepository.findByToken(userToken);
            if ((token.isEmpty()) ||(token.get().getExpired())) {
                // log invalid token
                return false;
            }

            user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                // add log saying the user is not present
                return false;
            }

        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
        authRepository.setAuthAsExpired(userToken);
        authRepository.setUserForToken(user.get().getId(), userToken);
        return true;
    }

  
    private void waitForUserIdAndNotify(AuthToken savedToken) {
        final int maxAttempts = 150;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                Optional<AuthToken> refreshed = authRepository.findByToken(savedToken.getToken());
                if (refreshed.isPresent() && refreshed.get().getUserId() != null && refreshed.get().getUserId() != 0) {
                    sendCallback(refreshed.get());
                    return;
                }
                Thread.sleep(1000);
                attempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("AuthService|waitForUserIdAndNotify|interrupted for token {}", savedToken.getToken());
                return;
            } catch (Exception e) {
                log.error("AuthService|waitForUserIdAndNotify|error|{}", e.toString());
            }
        }
        log.warn("AuthService|waitForUserIdAndNotify|timeout waiting for userId for token {}", savedToken.getToken());
    }

    private void sendCallback(AuthToken tokenWithUser) {
        String targetUrl = buildCallbackUrl(tokenWithUser.getCallbackUrl());
        if (targetUrl == null) {
            log.info("AuthService|sendCallback|no callback URL provided|token={} userId={}",
                    tokenWithUser.getToken(), tokenWithUser.getUserId());
            return;
        }
        CallbackPayload payload = new CallbackPayload(tokenWithUser.getToken(), tokenWithUser.getUserId());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json;charset=UTF-8");
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
            log.info("AuthService|sendCallback|sending|payload={} headers={}", jsonPayload, headers);
            restTemplate.postForEntity(targetUrl, request, Void.class);
            log.info("AuthService|sendCallback|success|url={} token={} userId={}",
                    targetUrl, tokenWithUser.getToken(), tokenWithUser.getUserId());
        } catch (RestClientException e) {
            log.error("AuthService|sendCallback|failure|url={} token={} userId={} error={}",
                    targetUrl, tokenWithUser.getToken(), tokenWithUser.getUserId(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("AuthService|sendCallback|error|{}", e.toString(), e);
        }
    }

    /**
     * Normalize the caller-provided callbackUrl into a usable URL.
     * Assumes the client passes either a full URL or a host[:port]/path.
     */
    private String buildCallbackUrl(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            return null;
        }
        if (callbackUrl.startsWith("http://") || callbackUrl.startsWith("https://")) {
            return callbackUrl;
        }
        return "http://" + callbackUrl;
    }

    private record CallbackPayload(String token, Integer userId) {}
}
