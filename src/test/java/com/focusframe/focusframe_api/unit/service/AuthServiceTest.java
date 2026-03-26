package com.focusframe.focusframe_api.unit.service;

import com.focusframe.focusframe_api.model.AuthToken;
import com.focusframe.focusframe_api.model.User;
import com.focusframe.focusframe_api.repository.AuthRepository;
import com.focusframe.focusframe_api.repository.UserRepository;
import com.focusframe.focusframe_api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        Executor noOpExecutor = runnable -> {
            // No-op executor keeps tests deterministic for methods that schedule async work.
        };
        authService = new AuthService(authRepository, userRepository, restTemplate, noOpExecutor);
    }

    @Test
    void authenticateTokenReturnsFalseWhenTokenNotFound() {
        when(authRepository.findByToken("missing-token")).thenReturn(Optional.empty());

        boolean result = authService.AuthenticateToken("missing-token", "user@focusframe.com");

        assertFalse(result);
        verify(authRepository, never()).setAuthAsExpired("missing-token");
    }

    @Test
    void authenticateTokenReturnsFalseWhenTokenExpired() {
        AuthToken token = new AuthToken("127.0.0.1", "http://callback", "expired-token");
        token.setExpired(true);

        when(authRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        boolean result = authService.AuthenticateToken("expired-token", "user@focusframe.com");

        assertFalse(result);
        verify(authRepository, never()).setAuthAsExpired("expired-token");
    }

    @Test
    void authenticateTokenReturnsTrueForValidTokenAndUser() {
        AuthToken token = new AuthToken("127.0.0.1", "http://callback", "valid-token");
        token.setExpired(false);
        User user = User.builder().id(44).email("user@focusframe.com").build();

        when(authRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail("user@focusframe.com")).thenReturn(Optional.of(user));

        boolean result = authService.AuthenticateToken("valid-token", "user@focusframe.com");

        assertTrue(result);
        verify(authRepository).setAuthAsExpired("valid-token");
        verify(authRepository).setUserForToken(44, "valid-token");
    }
}
