package com.focusframe.focusframe_api.unit.service;

import com.focusframe.focusframe_api.model.User;
import com.focusframe.focusframe_api.repository.UserRepository;
import com.focusframe.focusframe_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserThrowsWhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("existing@focusframe.com");

        when(userRepository.existsByEmail("existing@focusframe.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));

        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void findOrCreateUserReturnsExistingUser() {
        User existing = User.builder().id(7).email("user@focusframe.com").build();
        when(userRepository.findByEmail("user@focusframe.com")).thenReturn(Optional.of(existing));

        User result = userService.findOrCreateUser("user@focusframe.com");

        assertSame(existing, result);
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void findOrCreateUserCreatesWhenMissing() {
        User saved = User.builder().id(12).email("new@focusframe.com").build();
        when(userRepository.findByEmail("new@focusframe.com")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

        User result = userService.findOrCreateUser("new@focusframe.com");

        assertEquals(12, result.getId());
        assertEquals("new@focusframe.com", result.getEmail());
        verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
    }
}
