package org.example.daos;

import org.example.models.Product;
import java.util.List;
import java.util.Optional;

public interface IProductDAO {
    Product save(Product product);
    Optional<Product> findById(int id);
    List<Product> findAll();
}