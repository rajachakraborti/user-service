package com.carta.user.dto;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

public class UserProfileDTOTest {

    @Test
    void testConstructorsAndGettersSetters() {
        Instant now = Instant.now();
        UserProfileDTO dto = new UserProfileDTO(1L, "test@carta.com", "Test User", "ADMIN", now);

        assertEquals(1L, dto.getId());
        assertEquals("test@carta.com", dto.getEmail());
        assertEquals("Test User", dto.getFullName());
        assertEquals("ADMIN", dto.getRole());
        assertEquals(now, dto.getCreatedAt());

        UserProfileDTO emptyDto = new UserProfileDTO();
        emptyDto.setId(2L);
        emptyDto.setEmail("user2@carta.com");
        emptyDto.setFullName("User Two");
        emptyDto.setRole("DEVELOPER");
        emptyDto.setCreatedAt(now);

        assertEquals(2L, emptyDto.getId());
        assertEquals("user2@carta.com", emptyDto.getEmail());
        assertEquals("User Two", emptyDto.getFullName());
        assertEquals("DEVELOPER", emptyDto.getRole());
        assertEquals(now, emptyDto.getCreatedAt());
    }
}
