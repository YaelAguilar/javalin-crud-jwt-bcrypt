package org.example.services;

import org.example.configs.DbConfig;
import org.example.daos.ICartDAO;
import org.example.daos.ICartItemDAO;
import org.example.daos.IOrderDAO;
import org.example.daos.IOrderItemDAO;
import org.example.daos.IProductDAO;
import org.example.models.Cart;
import org.example.models.CartItem;
import org.example.models.Order;
import org.example.models.OrderItem;
import org.example.models.Product;
import org.example.models.dtos.order.OrderDetailDTO;
import org.example.models.dtos.order.OrderSummaryDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class OrderService {
    
    private final ICartDAO cartDAO;
    private final ICartItemDAO cartItemDAO;
    private final IOrderDAO orderDAO;
    private final IOrderItemDAO orderItemDAO;
    private final IProductDAO productDAO; // Necesario para actualizar stock

    public OrderService(ICartDAO cartDAO, ICartItemDAO cartItemDAO, IOrderDAO orderDAO, IOrderItemDAO orderItemDAO, IProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.productDAO = productDAO;
    }

    /**
     * Procesa el checkout para un usuario, convirtiendo su carrito en una orden.
     * Esta operación es transaccional.
     * @param userId El ID del usuario que realiza la compra.
     * @return Los detalles de la orden creada.
     */
    public OrderDetailDTO checkout(int userId) {
        try (Connection conn = DbConfig.getConnection()) {
            conn.setAutoCommit(false); // Iniciar la transacción
            
            Cart cart = cartDAO.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("El carrito del usuario está vacío o no existe."));

            List<CartItem> cartItems = cartItemDAO.findAllByCartId(cart.getId());
            if (cartItems.isEmpty()) {
                throw new IllegalArgumentException("El carrito está vacío, no se puede realizar la compra.");
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new java.util.ArrayList<>();

            // 1. Verificar stock y preparar OrderItems
            for (CartItem cartItem : cartItems) {
                Product product = productDAO.findById(cartItem.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("Producto '" + cartItem.getProductName() + "' (ID: " + cartItem.getProductId() + ") no encontrado."));

                if (product.getStock() < cartItem.getQuantity()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getName() + ". Solo quedan " + product.getStock() + " unidades.");
                }

                // Crear OrderItem (copia el estado actual del producto)
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductPrice(product.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItems.add(orderItem);

                totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            }

            // 2. Crear la Orden principal
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setStatus(Order.OrderStatus.PENDING); // Estado inicial
            order = orderDAO.save(conn, order); // Guardamos usando la conexión transaccional

            // 3. Guardar los OrderItems y actualizar el stock
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrderId(order.getId());
                orderItemDAO.save(conn, orderItem); // Guardamos usando la conexión transaccional

                // Actualizar stock del producto
                Product productToUpdate = productDAO.findById(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado para actualizar stock.")); // No debería pasar
                productToUpdate.setStock(productToUpdate.getStock() - orderItem.getQuantity());
                productDAO.update(productToUpdate) // Usamos la conexión del pool, se mantiene la transacción si la operación está aislada.
                          .orElseThrow(() -> new RuntimeException("No se pudo actualizar el stock del producto " + productToUpdate.getName()));
            }

            // 4. Vaciar el carrito
            cartItemDAO.deleteAllByCartId(cart.getId());

            conn.commit(); // Confirmar la transacción
            return mapOrderToDetailDTO(order, orderItems);

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos durante el checkout: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw e; // Relanzar para que el manejador de errores de Javalin lo capture
        }
    }

    /**
     * Obtiene el historial de órdenes de un usuario.
     */
    public List<OrderSummaryDTO> getUserOrders(int userId) {
        return orderDAO.findAllByUserId(userId).stream()
            .map(this::mapOrderToSummaryDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene los detalles de una orden específica de un usuario.
     */
    public OrderDetailDTO getUserOrderDetail(int userId, int orderId) {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new NoSuchElementException("Orden no encontrada."));
        
        if (order.getUserId() != userId) {
            throw new IllegalArgumentException("La orden no pertenece a este usuario.");
        }

        List<OrderItem> items = orderItemDAO.findAllByOrderId(orderId);
        return mapOrderToDetailDTO(order, items);
    }

    // Métodos de mapeo internos
    private OrderSummaryDTO mapOrderToSummaryDTO(Order order) {
        return new OrderSummaryDTO(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getOrderDate()
        );
    }

    private OrderDetailDTO mapOrderToDetailDTO(Order order, List<OrderItem> items) {
        return new OrderDetailDTO(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getOrderDate(),
            items
        );
    }
}