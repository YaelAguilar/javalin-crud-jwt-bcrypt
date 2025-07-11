package org.example.models;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private int orderId;    // Clave foránea a la tabla 'orders'
    private int productId;  // Clave foránea a la tabla 'products' (para referencia)
    private String productName; // Nombre del producto en el momento de la compra
    private BigDecimal productPrice; // Precio del producto en el momento de la compra
    private int quantity;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}