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
                // Todas las rutas del carrito requieren autenticación de un usuario
                ApiBuilder.before(AuthMiddleware.requireAuth());
                
                // GET /api/cart -> Ver mi carrito
                ApiBuilder.get(cartController::getCart);

                // DELETE /api/cart -> Vaciar mi carrito
                ApiBuilder.delete(cartController::clearCart);
                
                // POST /api/cart/items -> Añadir un item
                ApiBuilder.post("/items", cartController::addItem);

                // PUT /api/cart/items/{productId} -> Actualizar cantidad de un item
                ApiBuilder.put("/items/{productId}", cartController::updateItem);

                // DELETE /api/cart/items/{productId} -> Eliminar un item
                ApiBuilder.delete("/items/{productId}", cartController::removeItem);
            });
        });
    }
}