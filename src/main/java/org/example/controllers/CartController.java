package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.dtos.cart.CartItemAddDTO;
import org.example.models.dtos.cart.CartItemUpdateDTO;
import org.example.services.CartService;
import java.util.Map;
import java.util.NoSuchElementException;

public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    public void addItem(Context ctx) {
        try {
            int userId = ctx.attribute("userId");
            CartItemAddDTO itemDTO = ctx.bodyAsClass(CartItemAddDTO.class);
            
            var addedItem = cartService.addItemToCart(userId, itemDTO);
            ctx.status(200).json(Map.of("success", true, "message", "Item añadido al carrito.", "data", addedItem));
        } catch (NoSuchElementException e) {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    public void getCart(Context ctx) {
        try {
            int userId = ctx.attribute("userId");
            var cartView = cartService.getCartForUser(userId);
            ctx.status(200).json(Map.of("success", true, "data", cartView));
        } catch (NoSuchElementException e) {
            ctx.status(200).json(Map.of("success", true, "message", "El carrito está vacío.", "data", null));
        }
    }

    public void updateItem(Context ctx) {
        try {
            int userId = ctx.attribute("userId");
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            CartItemUpdateDTO updateDTO = ctx.bodyAsClass(CartItemUpdateDTO.class);

            var updatedItem = cartService.updateItemQuantity(userId, productId, updateDTO);
            ctx.status(200).json(Map.of("success", true, "message", "Cantidad actualizada.", "data", updatedItem));
        } catch (NoSuchElementException e) {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        }
    }

    public void removeItem(Context ctx) {
        try {
            int userId = ctx.attribute("userId");
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            
            cartService.removeItemFromCart(userId, productId);
            ctx.status(204); // No Content
        } catch (NoSuchElementException e) {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        }
    }
        
    public void clearCart(Context ctx) {
        try {
            int userId = ctx.attribute("userId");
            cartService.clearCart(userId);
            ctx.status(204); // No Content
        } catch (NoSuchElementException e) {
            // Si no hay carrito, la operación es igualmente exitosa (ya está vacío).
            ctx.status(204);
        } catch (Exception e) { //Capturamos el nuevo error
             System.err.println("Error al vaciar el carrito en CartController" + e.getMessage());
            ctx.status(500).json(Map.of("success", false, "message", "Error al procesar la compra: " + e.getMessage()));
        }
    }
}