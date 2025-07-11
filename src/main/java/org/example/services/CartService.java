package org.example.services;

import org.example.daos.ICartDAO;
import org.example.daos.ICartItemDAO;
import org.example.daos.IProductDAO;
import org.example.models.Cart;
import org.example.models.CartItem;
import org.example.models.Product;
import org.example.models.dtos.cart.CartItemAddDTO;
import org.example.models.dtos.cart.CartItemUpdateDTO;
import org.example.models.dtos.cart.CartViewDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

public class CartService {
    
    private final ICartDAO cartDAO;
    private final ICartItemDAO cartItemDAO;
    private final IProductDAO productDAO;

    public CartService(ICartDAO cartDAO, ICartItemDAO cartItemDAO, IProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.productDAO = productDAO;
    }

    // ... (método addItemToCart existente)
    public CartItem addItemToCart(int userId, CartItemAddDTO itemDTO) {
        // 1. Obtener o crear el carrito para el usuario
        Cart cart = cartDAO.findByUserId(userId).orElseGet(() -> cartDAO.createForUser(userId));

        // 2. Validar que el producto exista y tenga stock
        Product product = productDAO.findById(itemDTO.productId())
            .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + itemDTO.productId()));
        if (product.getStock() < itemDTO.quantity()) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getName());
        }

        // 3. Comprobar si el item ya existe en el carrito
        return cartItemDAO.findByCartIdAndProductId(cart.getId(), itemDTO.productId())
            .map(existingItem -> {
                // Si existe, actualiza la cantidad
                int newQuantity = existingItem.getQuantity() + itemDTO.quantity();
                if (product.getStock() < newQuantity) {
                    throw new IllegalArgumentException("Stock insuficiente para la cantidad total solicitada.");
                }
                existingItem.setQuantity(newQuantity);
                return cartItemDAO.update(existingItem).orElseThrow(() -> new RuntimeException("No se pudo actualizar el item en el carrito."));
            })
            .orElseGet(() -> {
                // Si no existe, crea un nuevo item
                CartItem newItem = new CartItem();
                newItem.setCartId(cart.getId());
                newItem.setProductId(itemDTO.productId());
                newItem.setQuantity(itemDTO.quantity());
                return cartItemDAO.save(newItem);
            });
    }

    public CartViewDTO getCartForUser(int userId) {
        Cart cart = cartDAO.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("El usuario no tiene un carrito activo."));

        List<CartItem> items = cartItemDAO.findAllByCartId(cart.getId());
        
        BigDecimal totalPrice = items.stream()
            .map(item -> item.getProductPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartViewDTO(cart.getId(), userId, items, totalPrice);
    }
    
    public CartItem updateItemQuantity(int userId, int productId, CartItemUpdateDTO updateDTO) {
        if (updateDTO.quantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        
        Cart cart = cartDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Carrito no encontrado."));
        Product product = productDAO.findById(productId).orElseThrow(() -> new NoSuchElementException("Producto no encontrado."));
        
        if (product.getStock() < updateDTO.quantity()) {
            throw new IllegalArgumentException("Stock insuficiente.");
        }

        CartItem item = cartItemDAO.findByCartIdAndProductId(cart.getId(), productId)
            .orElseThrow(() -> new NoSuchElementException("El producto no está en el carrito."));
            
        item.setQuantity(updateDTO.quantity());
        return cartItemDAO.update(item).orElseThrow(() -> new RuntimeException("No se pudo actualizar la cantidad."));
    }

    public void removeItemFromCart(int userId, int productId) {
        Cart cart = cartDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Carrito no encontrado."));
        
        if (!cartItemDAO.deleteByCartIdAndProductId(cart.getId(), productId)) {
            throw new NoSuchElementException("El producto no se encontró en el carrito.");
        }
    }
    
    public void clearCart(int userId) {
        Cart cart = cartDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Carrito no encontrado."));
        cartItemDAO.deleteAllByCartId(cart.getId());
    }
}