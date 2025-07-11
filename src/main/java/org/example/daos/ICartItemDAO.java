package org.example.daos;

import org.example.models.CartItem;
import java.util.List;
import java.util.Optional;

public interface ICartItemDAO {
    /**
     * Busca un item específico en un carrito por su ID de producto.
     * @param cartId El ID del carrito.
     * @param productId El ID del producto.
     * @return Un Optional con el CartItem si ya existe.
     */
    Optional<CartItem> findByCartIdAndProductId(int cartId, int productId);

    /**
     * Guarda un nuevo item en el carrito.
     * @param item El CartItem a guardar.
     * @return El CartItem guardado con su nuevo ID.
     */
    CartItem save(CartItem item);

    /**
     * Actualiza un item existente en el carrito (ej. su cantidad).
     * @param item El CartItem con los datos actualizados.
     * @return Un Optional con el CartItem actualizado.
     */
    Optional<CartItem> update(CartItem item);

    /**
     * Encuentra todos los items de un carrito específico.
     * @param cartId El ID del carrito.
     * @return Una lista de CartItem.
     */
    List<CartItem> findAllByCartId(int cartId);
}