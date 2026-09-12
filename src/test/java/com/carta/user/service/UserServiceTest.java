package com.carta.user.service;

import com.carta.user.context.TenantContext;
import com.carta.user.dao.UserDao;
import com.carta.user.dto.UserProfileDTO;
import com.carta.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testGetUserByEmailSuccess() throws SQLException {
        TenantContext.setTenantId("tenant_acme");
        User user = new User(10L, "tenant_acme", "test@acme.com", "Acme Tester", "MEMBER");
        when(userDao.findByEmail("tenant_acme", "test@acme.com")).thenReturn(Optional.of(user));

        Optional<UserProfileDTO> result = userService.getUserByEmail("test@acme.com");

        assertTrue(result.isPresent());
        assertEquals("test@acme.com", result.get().getEmail());
        assertEquals("Acme Tester", result.get().getFullName());
        verify(userDao).findByEmail("tenant_acme", "test@acme.com");
    }

    @Test
    void testGetUserByEmailThrowsWhenTenantContextEmpty() {
        TenantContext.clear();
        assertThrows(IllegalStateException.class, () -> userService.getUserByEmail("test@acme.com"));
    }
}
