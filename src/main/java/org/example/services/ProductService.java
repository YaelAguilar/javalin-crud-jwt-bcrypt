package org.example.services;

import org.example.daos.IProductDAO;
import org.example.models.Product;
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

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Product findProductById(int id) {
        return productDAO.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + id));
    }

    public Product updateProduct(int id, ProductUpdateDTO dto) {
        // Primero, buscamos si el producto existe. Esto también valida el ID.
        Product existingProduct = findProductById(id);

        // Actualizamos los campos del objeto existente
        existingProduct.setName(dto.name());
        existingProduct.setDescription(dto.description());
        existingProduct.setPrice(dto.price());
        existingProduct.setStock(dto.stock());

        // Guardamos los cambios en la base de datos
        return productDAO.update(existingProduct)
            .orElseThrow(() -> new RuntimeException("No se pudo actualizar el producto con ID: " + id));
    }

    public void deleteProduct(int id) {
        // Verificamos que el producto existe antes de intentar borrarlo.
        // Si no existe, findProductById lanzará una excepción.
        findProductById(id); 
        
        if (!productDAO.deleteById(id)) {
            throw new RuntimeException("No se pudo eliminar el producto con ID: " + id);
        }
    }
}