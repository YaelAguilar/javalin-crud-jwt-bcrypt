package org.example.daos.impl;

import org.example.configs.DbConfig;
import org.example.daos.IOrderItemDAO;
import org.example.models.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO implements IOrderItemDAO {

    @Override
    public OrderItem save(Connection conn, OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, product_name, product_price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, item.getOrderId());
            // product_id puede ser NULLABLE en la BD si el producto fue borrado, por eso usamos setObject
            if (item.getProductId() > 0) {
                pstmt.setInt(2, item.getProductId());
            } else {
                pstmt.setNull(2, Types.INTEGER); // Opcional, si permites producto_id null
            }
            pstmt.setString(3, item.getProductName());
            pstmt.setBigDecimal(4, item.getProductPrice());
            pstmt.setInt(5, item.getQuantity());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar item de la orden", e);
        }
    }

    @Override
    public List<OrderItem> findAllByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar items de la orden", e);
        }
        return items;
    }

    private OrderItem mapRowToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setProductId(rs.getInt("product_id")); // Será 0 si es NULL en la BD
        item.setProductName(rs.getString("product_name"));
        item.setProductPrice(rs.getBigDecimal("product_price"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}