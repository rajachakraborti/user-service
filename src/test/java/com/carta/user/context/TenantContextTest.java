package com.carta.user.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testSetAndGetTenantId() {
        assertNull(TenantContext.getTenantId());
        TenantContext.setTenantId("tenant_corp_100");
        assertEquals("tenant_corp_100", TenantContext.getTenantId());
    }

    @Test
    void testBlankTenantIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> TenantContext.setTenantId(null));
        assertThrows(IllegalArgumentException.class, () -> TenantContext.setTenantId("   "));
    }

    @Test
    void testClearRemovesContext() {
        TenantContext.setTenantId("tenant_temp");
        TenantContext.clear();
        assertNull(TenantContext.getTenantId());
    }
}
