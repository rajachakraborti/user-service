package com.devex.user.dao;

import com.devex.user.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

/**
 * Data access layer for User entities. Enforces strict multi-tenant table filtering.
 */
public class UserDao {

    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Finds a user by tenant ID and email address.
     * CRITICAL SECURITY INVARIANT: Must filter by tenant_id to prevent cross-tenant enumeration.
     */
    public Optional<User> findByEmail(String tenantId, String email) throws SQLException {
        String sql = "SELECT id, tenant_id, email, full_name, role, created_at, updated_at, active " +
                     "FROM users WHERE tenant_id = ? AND email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenantId);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getLong("id"),
                        rs.getString("tenant_id"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    );
                    user.setActive(rs.getBoolean("active"));
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(String tenantId, Long id) throws SQLException {
        String sql = "SELECT id, tenant_id, email, full_name, role, created_at, updated_at, active " +
                     "FROM users WHERE tenant_id = ? AND id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenantId);
            stmt.setLong(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("tenant_id"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void updateLastLogin(String tenantId, Long userId, Instant loginTime) throws SQLException {
        String sql = "UPDATE users SET updated_at = ? WHERE tenant_id = ? AND id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loginTime.toString());
            stmt.setString(2, tenantId);
            stmt.setLong(3, userId);
            stmt.executeUpdate();
        }
    }

    public int deleteInactiveUsers(String tenantId, Instant cutoffTime) throws SQLException {
        String sql = "DELETE FROM users WHERE tenant_id = ? AND active = false AND updated_at < ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenantId);
            stmt.setString(2, cutoffTime.toString());
            return stmt.executeUpdate();
        }
    }

    public long countActiveByTenant(String tenantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE tenant_id = ? AND active = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0L;
    }
}
