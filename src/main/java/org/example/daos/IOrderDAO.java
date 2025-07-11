package org.example.daos;

import org.example.models.Order;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface IOrderDAO {
    Order save(Connection conn, Order order); // Recibe una conexión para transacciones
    Optional<Order> findById(int id);
    List<Order> findAllByUserId(int userId);
    List<Order> findAll(); // Para administradores
}