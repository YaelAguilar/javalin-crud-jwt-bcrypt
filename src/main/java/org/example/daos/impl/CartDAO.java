package org.example.daos.impl;

import org.example.configs.DbConfig;
import org.example.daos.ICartDAO;
import org.example.models.Cart;

import java.sql.*;
import java.util.Optional;

public class CartDAO implements ICartDAO {

    @Override
    public Optional<Cart> findByUserId(int userId) {
        String sql = "SELECT * FROM carts WHERE user_id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCart(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar carrito por ID de usuario", e);
        }
        return Optional.empty();
    }

    @Override
    public Cart createForUser(int userId) {
        String sql = "INSERT INTO carts (user_id) VALUES (?)";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            Cart newCart = new Cart();
            newCart.setUserId(userId);
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newCart.setId(generatedKeys.getInt(1));
                }
            }
            return newCart;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear carrito para el usuario", e);
        }
    }

    private Cart mapRowToCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setId(rs.getInt("id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        cart.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return cart;
    }
}