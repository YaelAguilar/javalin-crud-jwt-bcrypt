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
        
        initDatabaseSchema();
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("El pool de conexiones (DataSource) no ha sido inicializado.");
        }
        return dataSource.getConnection();
    }

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
                "user_id INT NOT NULL UNIQUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB;";

        @Language("MySQL")
        String createCartItemsTableSQL = "CREATE TABLE IF NOT EXISTS cart_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "cart_id INT NOT NULL, " +
                "product_id INT NOT NULL, " +
                "quantity INT NOT NULL CHECK (quantity > 0), " +
                "FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE, " +
                "UNIQUE KEY (cart_id, product_id)" +
                ") ENGINE=InnoDB;";

        @Language("MySQL")
        String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "total_amount DECIMAL(10, 2) NOT NULL, " +
                "status VARCHAR(50) NOT NULL, " +
                "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT" +
                ") ENGINE=InnoDB;";

        @Language("MySQL")
        String createOrderItemsTableSQL = "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "order_id INT NOT NULL, " +
                "product_id INT, " +
                "product_name VARCHAR(255) NOT NULL, " +
                "product_price DECIMAL(10, 2) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE" +
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

            System.out.println("Verificando y/o creando tabla 'orders'...");
            stmt.execute(createOrdersTableSQL);
            System.out.println("Tabla 'orders' lista.");

            System.out.println("Verificando y/o creando tabla 'order_items'...");
            stmt.execute(createOrderItemsTableSQL);
            System.out.println("Tabla 'order_items' lista.");
            
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