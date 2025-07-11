package org.example.models.dtos.cart;

/**
 * DTO para la petición de añadir un item al carrito.
 */
public record CartItemAddDTO(int productId, int quantity) {}