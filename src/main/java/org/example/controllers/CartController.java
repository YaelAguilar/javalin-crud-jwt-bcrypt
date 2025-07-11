package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.dtos.cart.CartItemAddDTO;
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
            // Si un usuario nunca ha añadido nada, no tiene carrito. Esto es esperado.
            ctx.status(200).json(Map.of("success", true, "message", "El carrito está vacío.", "data", null));
        }
    }
}