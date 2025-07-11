package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.dtos.product.ProductCreateDTO;
import org.example.models.dtos.product.ProductUpdateDTO;
import org.example.services.ProductService;
import java.util.Map;

public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public void create(Context ctx) {
        try {
            ProductCreateDTO dto = ctx.bodyAsClass(ProductCreateDTO.class);
            var newProduct = productService.createProduct(dto);
            ctx.status(201).json(Map.of("success", true, "data", newProduct));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    public void getAll(Context ctx) {
        var products = productService.getAllProducts();
        ctx.status(200).json(Map.of("success", true, "data", products));
    }

    public void getOne(Context ctx) {
        // El manejador de excepciones de Main se encargará de NoSuchElementException (404)
        int productId = Integer.parseInt(ctx.pathParam("id"));
        var product = productService.findProductById(productId);
        ctx.status(200).json(Map.of("success", true, "data", product));
    }

    public void update(Context ctx) {
        int productId = Integer.parseInt(ctx.pathParam("id"));
        ProductUpdateDTO dto = ctx.bodyAsClass(ProductUpdateDTO.class);
        var updatedProduct = productService.updateProduct(productId, dto);
        ctx.status(200).json(Map.of("success", true, "data", updatedProduct));
    }

    public void delete(Context ctx) {
        int productId = Integer.parseInt(ctx.pathParam("id"));
        productService.deleteProduct(productId);
        ctx.status(204); // No Content
    }
}