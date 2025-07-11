package org.example.daos.impl;

import org.example.configs.DbConfig;
import org.example.daos.IProductDAO;
import org.example.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO implements IProductDAO {

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el producto", e);
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY created_at DESC";
        try (Connection conn = DbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar todos los productos", e);
        }
        return products;
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return p;
    }
}