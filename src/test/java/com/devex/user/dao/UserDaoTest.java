package com.devex.user.dao;

import com.devex.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDao(connection);
    }

    @Test
    void testFindByEmail_Success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("tenant_id")).thenReturn("tenant-alpha");
        when(resultSet.getString("email")).thenReturn("alice@alpha.com");
        when(resultSet.getString("full_name")).thenReturn("Alice Doe");
        when(resultSet.getString("role")).thenReturn("ADMIN");
        when(resultSet.getBoolean("active")).thenReturn(true);

        Optional<User> user = userDao.findByEmail("tenant-alpha", "alice@alpha.com");

        assertTrue(user.isPresent());
        assertEquals("alice@alpha.com", user.get().getEmail());
        assertEquals("tenant-alpha", user.get().getTenantId());
        assertTrue(user.get().isActive());
        verify(preparedStatement).setString(1, "tenant-alpha");
        verify(preparedStatement).setString(2, "alice@alpha.com");
    }

    @Test
    void testFindByEmail_NotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<User> user = userDao.findByEmail("tenant-alpha", "ghost@alpha.com");

        assertFalse(user.isPresent());
    }

    @Test
    void testFindById_Success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(42L);
        when(resultSet.getString("tenant_id")).thenReturn("tenant-beta");
        when(resultSet.getString("email")).thenReturn("bob@beta.com");
        when(resultSet.getString("full_name")).thenReturn("Bob Smith");
        when(resultSet.getString("role")).thenReturn("VIEWER");

        Optional<User> user = userDao.findById("tenant-beta", 42L);

        assertTrue(user.isPresent());
        assertEquals(42L, user.get().getId());
        assertEquals("bob@beta.com", user.get().getEmail());
        verify(preparedStatement).setString(1, "tenant-beta");
        verify(preparedStatement).setLong(2, 42L);
    }

    @Test
    void testFindById_NotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<User> user = userDao.findById("tenant-beta", 999L);

        assertFalse(user.isPresent());
    }

    @Test
    void testUpdateLastLogin() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        Instant now = Instant.now();

        userDao.updateLastLogin("tenant-gamma", 10L, now);

        verify(preparedStatement).setString(1, now.toString());
        verify(preparedStatement).setString(2, "tenant-gamma");
        verify(preparedStatement).setLong(3, 10L);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDeleteInactiveUsers() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(5);
        Instant cutoff = Instant.now();

        int deleted = userDao.deleteInactiveUsers("tenant-delta", cutoff);

        assertEquals(5, deleted);
        verify(preparedStatement).setString(1, "tenant-delta");
        verify(preparedStatement).setString(2, cutoff.toString());
    }

    @Test
    void testCountActiveByTenant() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(12L);

        long count = userDao.countActiveByTenant("tenant-epsilon");

        assertEquals(12L, count);
        verify(preparedStatement).setString(1, "tenant-epsilon");
    }

    @Test
    void testCountActiveByTenant_Empty() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        long count = userDao.countActiveByTenant("tenant-epsilon");

        assertEquals(0L, count);
    }
}
