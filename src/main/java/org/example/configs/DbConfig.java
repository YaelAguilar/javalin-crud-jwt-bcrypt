package org.example.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConfig {
    private static HikariDataSource dataSource;

    public static void init() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConfig.getDbUrl());
        config.setUsername(AppConfig.getDbUsername());
        config.setPassword(AppConfig.getDbPassword());
        config.setMaximumPoolSize(AppConfig.getDbMaxPoolSize());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        
        // Inicializar el esquema de la base de datos (crear tablas si no existen)
        initDatabaseSchema();
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("El pool de conexiones (DataSource) no ha sido inicializado.");
        }
        return dataSource.getConnection();
    }

    /**
     * Este método se encarga de crear las tablas necesarias si no existen.
     */
    private static void initDatabaseSchema() {
        @Language("MySQL")
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "INDEX idx_email (email)) ENGINE=InnoDB;";
        
        @Language("MySQL")
        String createProductsTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "stock INT NOT NULL DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB;";

        @Language("MySQL")
        String createCartsTableSQL = "CREATE TABLE IF NOT EXISTS carts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL UNIQUE, " + // Un usuario solo tiene un carrito
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" + // Si se borra el usuario, se borra su carrito
                ") ENGINE=InnoDB;";

        @Language("MySQL")
        String createCartItemsTableSQL = "CREATE TABLE IF NOT EXISTS cart_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "cart_id INT NOT NULL, " +
                "product_id INT NOT NULL, " +
                "quantity INT NOT NULL CHECK (quantity > 0), " +
                "FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE, " + // Si se borra el carrito, se borran sus items
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE, " + // Si se borra el producto, se quita de los carritos
                "UNIQUE KEY (cart_id, product_id)" + // Un producto solo puede aparecer una vez por carrito
                ") ENGINE=InnoDB;";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            System.out.println("Verificando y/o creando tabla 'users'...");
            stmt.execute(createUsersTableSQL);
            System.out.println("Tabla 'users' lista.");
            
            System.out.println("Verificando y/o creando tabla 'products'...");
            stmt.execute(createProductsTableSQL);
            System.out.println("Tabla 'products' lista.");
            
            System.out.println("Verificando y/o creando tabla 'carts'...");
            stmt.execute(createCartsTableSQL);
            System.out.println("Tabla 'carts' lista.");

            System.out.println("Verificando y/o creando tabla 'cart_items'...");
            stmt.execute(createCartItemsTableSQL);
            System.out.println("Tabla 'cart_items' lista.");
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar el esquema de la base de datos: " + e.getMessage());
            throw new RuntimeException("Error fatal durante la inicialización de la BD.", e);
        }
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexiones cerrado.");
        }
    }
}