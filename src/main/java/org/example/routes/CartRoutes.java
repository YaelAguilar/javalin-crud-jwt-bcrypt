package org.example.routes;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import org.example.controllers.CartController;
import org.example.middlewares.AuthMiddleware;

public class CartRoutes {
    private final CartController cartController;

    public CartRoutes(CartController cartController) {
        this.cartController = cartController;
    }

    public void register(Javalin app) {
        app.routes(() -> {
            ApiBuilder.path("/api/cart", () -> {
                // Todas las rutas del carrito requieren autenticación
                ApiBuilder.before(AuthMiddleware.requireAuth());
                
                // GET /api/cart -> Ver mi carrito
                ApiBuilder.get(cartController::getCart);
                
                // POST /api/cart/items -> Añadir un item al carrito
                ApiBuilder.post("/items", cartController::addItem);
            });
        });
    }
}