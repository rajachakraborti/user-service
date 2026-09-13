package com.devex.user.controller;

import com.devex.user.context.TenantContext;
import com.devex.user.dto.UserProfileDTO;
import com.devex.user.service.UserService;
import java.util.Optional;

/**
 * Spring MVC REST Controller for User domain operations.
 * Extracts tenant identification from HTTP headers and enforces context lifecycle.
 */
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves user profile by email within the caller's tenant boundary.
     *
     * @param tenantId HTTP header "X-Tenant-ID" identifying caller's tenant boundary
     * @param email Target user email
     * @return UserProfileDTO or empty
     */
    public Optional<UserProfileDTO> getUserProfile(
            String tenantId,
            String email) {

        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Header X-Tenant-ID is mandatory");
        }

        try {
            TenantContext.setTenantId(tenantId);
            return userService.getUserByEmail(email);
        } finally {
            TenantContext.clear();
        }
    }
}
