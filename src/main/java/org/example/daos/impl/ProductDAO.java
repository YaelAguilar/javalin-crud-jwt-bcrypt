package org.example.daos.impl;

import org.example.configs.DbConfig;
import org.example.daos.IProductDAO;
import org.example.models.Product;
import org.intellij.lang.annotations.Language;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO implements IProductDAO {

    @Override
    public Product save(Product product) {
        @Language("MySQL")
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
                } else {
                    throw new SQLException("La creación del producto falló, no se obtuvo ID.");
                }
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el producto", e);
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        @Language("MySQL")
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Product> update(Product product) {
        @Language("MySQL")
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            pstmt.setInt(5, product.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el producto", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(int id) {
        @Language("MySQL")
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto", e);
        }
    }
        
    @Override
    public List<Product> findPaginated(int offset, int limit) {
        List<Product> products = new ArrayList<>();
        @Language("MySQL")
        String sql = "SELECT * FROM products ORDER BY id ASC LIMIT ? OFFSET ?";
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productos paginados", e);
        }
        return products;
    }

    @Override
    public long count() {
        @Language("MySQL")
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar los productos", e);
        }
        return 0;
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