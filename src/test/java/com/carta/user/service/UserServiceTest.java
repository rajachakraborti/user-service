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
    void testGetUserByEmailNotFound() throws SQLException {
        TenantContext.setTenantId("tenant_acme");
        when(userDao.findByEmail("tenant_acme", "none@acme.com")).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userService.getUserByEmail("none@acme.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmailDatabaseError() throws SQLException {
        TenantContext.setTenantId("tenant_acme");
        when(userDao.findByEmail("tenant_acme", "err@acme.com")).thenThrow(new SQLException("DB Connection dropped"));

        assertThrows(RuntimeException.class, () -> userService.getUserByEmail("err@acme.com"));
    }

    @Test
    void testGetUserByEmailThrowsWhenTenantContextEmpty() {
        TenantContext.clear();
        assertThrows(IllegalStateException.class, () -> userService.getUserByEmail("test@acme.com"));
    }

    @Test
    void testGetUserByIdSuccess() throws SQLException {
        TenantContext.setTenantId("tenant_acme");
        User user = new User(25L, "tenant_acme", "id@acme.com", "ID User", "ADMIN");
        when(userDao.findById("tenant_acme", 25L)).thenReturn(Optional.of(user));

        Optional<UserProfileDTO> result = userService.getUserById(25L);

        assertTrue(result.isPresent());
        assertEquals(25L, result.get().getId());
        assertEquals("id@acme.com", result.get().getEmail());
        verify(userDao).findById("tenant_acme", 25L);
    }

    @Test
    void testGetUserByIdNotFound() throws SQLException {
        TenantContext.setTenantId("tenant_acme");
        when(userDao.findById("tenant_acme", 99L)).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userService.getUserById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByIdDatabaseError() throws SQLException {
        TenantContext.setTenantId("tenant_acme");
        when(userDao.findById("tenant_acme", 99L)).thenThrow(new SQLException("Query timeout"));

        assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
    }

    @Test
    void testGetUserByIdThrowsWhenTenantContextEmpty() {
        TenantContext.clear();
        assertThrows(IllegalStateException.class, () -> userService.getUserById(1L));
    }
}
