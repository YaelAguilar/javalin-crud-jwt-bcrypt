package org.example.models.dtos.user;

import org.example.models.Role;
import java.time.LocalDateTime;

public record UserDTO(int id, String name, String email, Role role, LocalDateTime createdAt) {}