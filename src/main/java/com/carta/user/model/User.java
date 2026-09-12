package com.carta.user.model;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Domain entity representing a user in a multi-tenant account.
 */
public class User {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private Long id;
    private String tenantId;
    private String email;
    private String fullName;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;

    public User() {
    }

    public User(Long id, String tenantId, String email, String fullName, String role) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.active = true;
    }

    /**
     * Isolated helper method validating email syntax.
     */
    public boolean isValidEmail(String candidateEmail) {
        if (candidateEmail == null || candidateEmail.trim().isEmpty()) {
            return false;
        }
        return EMAIL_REGEX.matcher(candidateEmail.trim()).matches();
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equalsIgnoreCase(this.role);
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(tenantId, user.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId);
    }
}
