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
                // Ruta pública para listar productos
                ApiBuilder.get(productController::getAll);

                // Ruta protegida para crear productos (solo ADMINS)
                ApiBuilder.post(ctx -> {
                    // Aplicamos los middlewares en la misma línea
                    AuthMiddleware.requireAuth().handle(ctx);
                    AuthMiddleware.requireAdmin().handle(ctx);
                    productController.create(ctx);
                });
            });
        });
    }
}