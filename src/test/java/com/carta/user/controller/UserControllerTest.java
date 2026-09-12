package com.carta.user.controller;

import com.carta.user.context.TenantContext;
import com.carta.user.dto.UserProfileDTO;
import com.carta.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetUserProfileSuccess() {
        UserProfileDTO dto = new UserProfileDTO(1L, "john@example.com", "John Doe", "USER", Instant.now());
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(dto));

        Optional<UserProfileDTO> res = userController.getUserProfile("tenant_123", "john@example.com");

        assertTrue(res.isPresent());
        assertEquals("john@example.com", res.get().getEmail());
        // Verify tenant context is cleaned up after request
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void testGetUserProfileThrowsOnBlankTenantId() {
        assertThrows(IllegalArgumentException.class, () -> userController.getUserProfile(null, "john@example.com"));
        assertThrows(IllegalArgumentException.class, () -> userController.getUserProfile("", "john@example.com"));
        assertThrows(IllegalArgumentException.class, () -> userController.getUserProfile("   ", "john@example.com"));
    }
}
