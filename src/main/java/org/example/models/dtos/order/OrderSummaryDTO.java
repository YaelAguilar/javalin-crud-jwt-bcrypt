package org.example.models.dtos.order;

import org.example.models.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryDTO(
    int id,
    int userId,
    BigDecimal totalAmount,
    Order.OrderStatus status,
    LocalDateTime orderDate
) {}