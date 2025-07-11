package org.example.routes;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import org.example.controllers.ProductController;
import org.example.middlewares.AuthMiddleware;

public class ProductRoutes {
    private final ProductController productController;

    public ProductRoutes(ProductController productController) {
        this.productController = productController;
    }

    public void register(Javalin app) {
        app.routes(() -> {
            ApiBuilder.path("/api/products", () -> {
                // --- Rutas Públicas (no requieren autenticación) ---
                ApiBuilder.get(productController::getAll); // GET /api/products
                ApiBuilder.get("/{id}", productController::getOne); // GET /api/products/{id}

                // --- Rutas de Administración (requieren autenticación y rol ADMIN) ---
                // Agrupamos estas rutas para aplicarles los middlewares.
                // Usamos un 'path("")' anidado para aplicar los BEFORE solo a este subgrupo
                // (POST, PUT, DELETE) y no a los GET públicos de arriba.
                ApiBuilder.path("", () -> { // Este path vacío significa "/api/products"
                    ApiBuilder.before(AuthMiddleware.requireAuth()); // Primero autenticación
                    ApiBuilder.before(AuthMiddleware.requireAdmin()); // Luego autorización ADMIN

                    ApiBuilder.post(productController::create); // POST /api/products
                    ApiBuilder.put("/{id}", productController::update); // PUT /api/products/{id}
                    ApiBuilder.delete("/{id}", productController::delete); // DELETE /api/products/{id}
                });
            });
        });
    }
}