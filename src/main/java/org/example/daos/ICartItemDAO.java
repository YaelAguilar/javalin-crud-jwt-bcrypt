package org.example.daos;

import org.example.models.CartItem;
import java.util.List;
import java.util.Optional;

public interface ICartItemDAO {
    Optional<CartItem> findByCartIdAndProductId(int cartId, int productId);
    CartItem save(CartItem item);
    Optional<CartItem> update(CartItem item);
    List<CartItem> findAllByCartId(int cartId);
    boolean deleteByCartIdAndProductId(int cartId, int productId);
    void deleteAllByCartId(int cartId);
}