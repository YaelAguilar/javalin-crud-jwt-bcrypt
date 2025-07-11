package org.example.models.dtos.product;

import java.math.BigDecimal;

public record ProductCreateDTO(String name, String description, BigDecimal price, int stock) {}