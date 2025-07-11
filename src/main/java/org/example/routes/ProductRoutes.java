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
                
                // --- Rutas Públicas ---
                // GET /api/products -> Listar todos
                ApiBuilder.get(productController::getAll);
                // GET /api/products/{id} -> Obtener uno
                ApiBuilder.get("/{id}", productController::getOne);

                // --- Rutas de Administrador ---
                // Agrupamos las rutas que requieren permisos de administrador.
                // Esta es una forma más limpia de aplicar middlewares a un grupo.
                ApiBuilder.path("", () -> {
                    // Aplicamos middlewares a este sub-grupo.
                    // Cualquier petición a POST, PUT, DELETE en /api/products requerirá autenticación de admin.
                    ApiBuilder.before(AuthMiddleware.requireAuth());
                    ApiBuilder.before(AuthMiddleware.requireAdmin());
                    
                    // POST /api/products -> Crear
                    ApiBuilder.post(productController::create);
                    // PUT /api/products/{id} -> Actualizar
                    ApiBuilder.put("/{id}", productController::update);
                    // DELETE /api/products/{id} -> Eliminar
                    ApiBuilder.delete("/{id}", productController::delete);
                });
            });
        });
    }
}