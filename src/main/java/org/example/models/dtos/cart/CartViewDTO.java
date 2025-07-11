package org.example.models.dtos.cart;

import org.example.models.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para devolver la vista completa del carrito de un usuario.
 */
public record CartViewDTO(
    int cartId,
    int userId,
    List<CartItem> items,
    BigDecimal totalPrice
) {}