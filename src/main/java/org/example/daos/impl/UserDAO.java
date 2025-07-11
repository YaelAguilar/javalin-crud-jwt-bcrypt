package org.example.daos.impl;

import org.example.configs.DbConfig;
import org.example.daos.IUserDAO;
import org.example.models.Role;
import org.example.models.User;
import org.intellij.lang.annotations.Language;

import java.sql.*;
import java.util.Optional;

public class UserDAO implements IUserDAO {

    @Override
    public Optional<User> findByEmail(String email) {
        @Language("MySQL")
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        @Language("MySQL")
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear el usuario, ninguna fila afectada.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Fallo al crear usuario, no se obtuvo el ID.");
                }
            }
            return user;
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            throw new RuntimeException("Error de base de datos al guardar el usuario.", e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        @Language("MySQL")
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por id: " + e.getMessage());
        }
        return Optional.empty();
    }


    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}