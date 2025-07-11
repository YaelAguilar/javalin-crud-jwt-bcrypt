package org.example.models.dtos.common;

import java.util.List;

/**
 * Un DTO genérico para encapsular respuestas paginadas.
 * @param <T> El tipo de los datos en la lista.
 */
public record PaginatedResponseDTO<T>(
    List<T> data,
    int currentPage,
    int pageSize,
    long totalItems,
    int totalPages
) {}