package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Order;
import com.abhi.FitnessTracker.Repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for admin order management.
 * Allows viewing, creating, updating, and deleting orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Get all orders
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        // Sort by createdAt descending (newest first)
        orders.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by userId
     * GET /api/orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable String userId) {
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }

    /**
     * Get a single order by ID
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new order (admin)
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            if (order.getCreatedAt() == 0) {
                order.setCreatedAt(System.currentTimeMillis());
            }
            if (order.getStatus() == null || order.getStatus().isEmpty()) {
                order.setStatus("CREATED");
            }
            Order saved = orderRepository.save(order);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update an order
     * PUT /api/orders/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody Order order) {
        try {
            if (!orderRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            order.setId(id);
            Order updated = orderRepository.save(order);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete an order
     * DELETE /api/orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        try {
            if (!orderRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            orderRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Order deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
