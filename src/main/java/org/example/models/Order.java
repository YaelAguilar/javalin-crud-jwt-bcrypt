package org.example.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    public enum OrderStatus {
        PENDING,    // Pedido recibido, esperando procesamiento
        PROCESSING, // En preparación o envío
        COMPLETED,  // Entregado
        CANCELLED   // Cancelado
    }

    private int id;
    private int userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
}