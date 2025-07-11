package org.example.daos;

import org.example.models.Cart;
import java.util.Optional;

public interface ICartDAO {
    /**
     * Busca un carrito por el ID del usuario.
     * @param userId El ID del usuario.
     * @return Un Optional con el Cart si existe.
     */
    Optional<Cart> findByUserId(int userId);

    /**
     * Crea un nuevo carrito para un usuario.
     * @param userId El ID del usuario para quien se crea el carrito.
     * @return El Cart recién creado.
     */
    Cart createForUser(int userId);
}