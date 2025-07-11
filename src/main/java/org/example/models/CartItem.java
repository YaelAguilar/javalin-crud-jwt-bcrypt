package org.example.models;

import java.math.BigDecimal;

public class CartItem {
    private int id;
    private int cartId;     // Clave foránea a la tabla 'carts'
    private int productId;  // Clave foránea a la tabla 'products'
    private int quantity;
    
    // Estos campos no se persisten, se unen desde la tabla de productos para facilitar la respuesta.
    private String productName;
    private BigDecimal productPrice;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
}