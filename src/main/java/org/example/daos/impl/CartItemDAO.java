package org.example.daos.impl;

import org.example.configs.DbConfig;
import org.example.daos.ICartItemDAO;
import org.example.models.CartItem;

import java.sql.*;
import java.util.Optional;

public class CartItemDAO implements ICartItemDAO {

    @Override
    public Optional<CartItem> findByCartIdAndProductId(int cartId, int productId) {
        String sql = "SELECT * FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCartItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar item en el carrito", e);
        }
        return Optional.empty();
    }

    @Override
    public CartItem save(CartItem item) {
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, item.getCartId());
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar item en el carrito", e);
        }
    }

    @Override
    public Optional<CartItem> update(CartItem item) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, item.getQuantity());
            pstmt.setInt(2, item.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar item en el carrito", e);
        }
        return Optional.empty();
    }

    private CartItem mapRowToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getInt("id"));
        item.setCartId(rs.getInt("cart_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}