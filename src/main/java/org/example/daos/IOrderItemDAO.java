package org.example.daos;

import org.example.models.OrderItem;
import java.sql.Connection;
import java.util.List;

public interface IOrderItemDAO {
    OrderItem save(Connection conn, OrderItem item); // Recibe una conexión para transacciones
    List<OrderItem> findAllByOrderId(int orderId);
}