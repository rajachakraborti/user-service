package com.carta.user.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testValidEmailRecognition() {
        User user = new User(1L, "tenant_a", "alice@example.com", "Alice Smith", "USER");
        assertTrue(user.isValidEmail("alice@example.com"));
        assertTrue(user.isValidEmail("bob.builder+test@sub.domain.org"));
        assertFalse(user.isValidEmail("invalid-email"));
        assertFalse(user.isValidEmail(""));
        assertFalse(user.isValidEmail("   "));
        assertFalse(user.isValidEmail(null));
    }

    @Test
    void testSuperAdminCheck() {
        User admin = new User(2L, "tenant_a", "admin@example.com", "Admin", "SUPER_ADMIN");
        assertTrue(admin.isSuperAdmin());

        User regular = new User(3L, "tenant_a", "user@example.com", "Regular", "USER");
        assertFalse(regular.isSuperAdmin());
    }

    @Test
    void testDeactivate() {
        User user = new User(4L, "tenant_a", "user@example.com", "User", "USER");
        assertTrue(user.isActive());
        user.deactivate();
        assertFalse(user.isActive());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        Instant now = Instant.now();

        user.setId(100L);
        user.setTenantId("tenant_xyz");
        user.setEmail("test@xyz.com");
        user.setFullName("Full Name");
        user.setRole("DEVELOPER");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setActive(true);

        assertEquals(100L, user.getId());
        assertEquals("tenant_xyz", user.getTenantId());
        assertEquals("test@xyz.com", user.getEmail());
        assertEquals("Full Name", user.getFullName());
        assertEquals("DEVELOPER", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertTrue(user.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        User u1 = new User(5L, "tenant_a", "u@ex.com", "U", "USER");
        User u2 = new User(5L, "tenant_a", "u@ex.com", "U", "USER");
        User u3 = new User(6L, "tenant_a", "u@ex.com", "U", "USER");
        User u4 = new User(5L, "tenant_b", "u@ex.com", "U", "USER");

        assertEquals(u1, u1);
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1, u3);
        assertNotEquals(u1, u4);
        assertNotEquals(u1, null);
        assertNotEquals(u1, "string_object");
    }
}
