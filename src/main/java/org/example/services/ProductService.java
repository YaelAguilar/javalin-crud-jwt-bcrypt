package org.example.services;

import org.example.daos.IProductDAO;
import org.example.models.Product;
import org.example.models.dtos.product.ProductCreateDTO;

import java.util.List;

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

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
}