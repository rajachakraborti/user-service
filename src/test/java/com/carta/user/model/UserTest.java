package com.carta.user.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testValidEmailRecognition() {
        User user = new User(1L, "tenant_a", "alice@example.com", "Alice Smith", "USER");
        assertTrue(user.isValidEmail("alice@example.com"));
        assertTrue(user.isValidEmail("bob.builder+test@sub.domain.org"));
        assertFalse(user.isValidEmail("invalid-email"));
        assertFalse(user.isValidEmail(""));
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
    void testEqualsAndHashCode() {
        User u1 = new User(5L, "tenant_a", "u@ex.com", "U", "USER");
        User u2 = new User(5L, "tenant_a", "u@ex.com", "U", "USER");
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
