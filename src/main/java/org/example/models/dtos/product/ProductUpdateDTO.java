package org.example.models.dtos.product;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para manejar los datos de actualización de un producto.
 * Usamos un 'record' para una definición inmutable y concisa.
 */
public record ProductUpdateDTO(
    String name,
    String description,
    BigDecimal price,
    int stock
) {}