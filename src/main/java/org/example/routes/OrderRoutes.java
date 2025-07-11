package org.example.routes;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import org.example.controllers.OrderController;
import org.example.middlewares.AuthMiddleware;

public class OrderRoutes {
    private final OrderController orderController;

    public OrderRoutes(OrderController orderController) {
        this.orderController = orderController;
    }

    public void register(Javalin app) {
        app.routes(() -> {
            ApiBuilder.path("/api/orders", () -> {
                // Todas las rutas de órdenes requieren autenticación
                ApiBuilder.before(AuthMiddleware.requireAuth());
                
                // POST /api/orders -> Realizar checkout
                ApiBuilder.post(orderController::checkout);

                // GET /api/orders -> Obtener historial de órdenes del usuario
                ApiBuilder.get(orderController::getUserOrders);

                // GET /api/orders/{orderId} -> Obtener detalles de una orden específica
                ApiBuilder.get("/{orderId}", orderController::getUserOrderDetail);
            });
        });
    }
}