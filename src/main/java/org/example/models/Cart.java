package org.example.models;

import java.time.LocalDateTime;

public class Cart {
    private int id;
    private int userId; // Clave foránea a la tabla 'users'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}