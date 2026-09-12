package com.carta.user.service;

import com.carta.user.context.TenantContext;
import com.carta.user.dao.UserDao;
import com.carta.user.dto.UserProfileDTO;
import com.carta.user.model.User;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Service layer coordinating user lookup, security context validation, and DTO projection.
 */
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Resolves user profile for an email under the currently authenticated tenant.
     */
    public Optional<UserProfileDTO> getUserByEmail(String email) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalStateException("Security breach attempt: TenantContext has no active tenant");
        }

        try {
            Optional<User> userOptional = userDao.findByEmail(tenantId, email);
            return userOptional.map(this::toProfileDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving user by email", e);
        }
    }

    public Optional<UserProfileDTO> getUserById(Long id) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalStateException("Missing tenant context");
        }

        try {
            return userDao.findById(tenantId, id).map(this::toProfileDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving user by id", e);
        }
    }

    private UserProfileDTO toProfileDTO(User user) {
        return new UserProfileDTO(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
