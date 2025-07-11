package org.example.models.dtos.order;

import org.example.models.Order;
import org.example.models.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailDTO(
    int id,
    int userId,
    BigDecimal totalAmount,
    Order.OrderStatus status,
    LocalDateTime orderDate,
    List<OrderItem> items // Incluye los detalles de los items de la orden
) {}