package org.example.controllers;

import io.javalin.http.Context;
import org.example.services.OrderService;
import java.util.Map;
import java.util.NoSuchElementException;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void checkout(Context ctx) {
        try {
            int userId = ctx.attribute("userId");
            var orderDetail = orderService.checkout(userId);
            ctx.status(201).json(Map.of("success", true, "message", "Compra realizada con éxito.", "data", orderDetail));
        } catch (NoSuchElementException e) {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        } catch (RuntimeException e) { // Captura otros errores internos del servicio (ej. stock, DB)
            ctx.status(500).json(Map.of("success", false, "message", "Error al procesar la compra: " + e.getMessage()));
        }
    }

    public void getUserOrders(Context ctx) {
        int userId = ctx.attribute("userId");
        var orders = orderService.getUserOrders(userId);
        ctx.status(200).json(Map.of("success", true, "data", orders));
    }

    public void getUserOrderDetail(Context ctx) {
        int userId = ctx.attribute("userId");
        int orderId = Integer.parseInt(ctx.pathParam("orderId"));
        try {
            var orderDetail = orderService.getUserOrderDetail(userId, orderId);
            ctx.status(200).json(Map.of("success", true, "data", orderDetail));
        } catch (NoSuchElementException e) {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) { // Si la orden no pertenece al usuario
            ctx.status(403).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
}