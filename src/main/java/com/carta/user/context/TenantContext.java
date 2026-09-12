package com.carta.user.context;

/**
 * Thread-local security context storing current authenticated tenant identity.
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void setTenantId(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank");
        }
        CURRENT_TENANT.set(tenantId.trim());
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
