package org.example.services;

import org.example.daos.IProductDAO;
import org.example.models.Product;
import org.example.models.dtos.common.PaginatedResponseDTO;
import org.example.models.dtos.product.ProductCreateDTO;
import org.example.models.dtos.product.ProductUpdateDTO;

import java.util.List;
import java.util.NoSuchElementException;

public class ProductService {
    private final IProductDAO productDAO;

    public ProductService(IProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product createProduct(ProductCreateDTO dto) {
        if (dto.name() == null || dto.name().isBlank() || dto.price() == null || dto.price().signum() < 0) {
            throw new IllegalArgumentException("Nombre y precio son obligatorios y el precio no puede ser negativo.");
        }
        Product newProduct = new Product();
        newProduct.setName(dto.name());
        newProduct.setDescription(dto.description());
        newProduct.setPrice(dto.price());
        newProduct.setStock(dto.stock());

        return productDAO.save(newProduct);
    }

    public PaginatedResponseDTO<Product> getAllProductsPaginated(int page, int pageSize) {
        // Validar para evitar valores negativos que rompan el cálculo del offset
        if (page <= 0) page = 1;
        if (pageSize <= 0) pageSize = 10;

        int offset = (page - 1) * pageSize;

        List<Product> products = productDAO.findPaginated(offset, pageSize);
        long totalItems = productDAO.count();

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0 && totalItems > 0) { // Caso borde para pocos items
            totalPages = 1;
        }

        return new PaginatedResponseDTO<>(products, page, pageSize, totalItems, totalPages);
    }

    public Product findProductById(int id) {
        return productDAO.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + id));
    }

    public Product updateProduct(int id, ProductUpdateDTO dto) {
        Product existingProduct = findProductById(id);

        existingProduct.setName(dto.name());
        existingProduct.setDescription(dto.description());
        existingProduct.setPrice(dto.price());
        existingProduct.setStock(dto.stock());

        return productDAO.update(existingProduct)
            .orElseThrow(() -> new RuntimeException("No se pudo actualizar el producto con ID: " + id));
    }

    public void deleteProduct(int id) {
        findProductById(id);
        
        if (!productDAO.deleteById(id)) {
            throw new RuntimeException("No se pudo eliminar el producto con ID: " + id);
        }
    }
}