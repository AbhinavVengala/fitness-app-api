package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Order;
import com.abhi.FitnessTracker.Repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId("order-1");
        testOrder.setUserId("user-1");
        testOrder.setTotalAmount(250.0);
        testOrder.setStatus("CREATED");
        testOrder.setCreatedAt(System.currentTimeMillis());
    }

    @Test
    void getAllOrders_returnsSortedList() {
        Order older = new Order();
        older.setCreatedAt(1000);
        Order newer = new Order();
        newer.setCreatedAt(2000);

        when(orderRepository.findAll()).thenReturn(new ArrayList<>(List.of(older, newer)));

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Newest first
        assertEquals(2000, response.getBody().get(0).getCreatedAt());
    }

    @Test
    void getOrdersByUser_returnsList() {
        when(orderRepository.findByUserId("user-1")).thenReturn(List.of(testOrder));

        ResponseEntity<List<Order>> response = orderController.getOrdersByUser("user-1");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getOrder_found_returnsOrder() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(testOrder));

        ResponseEntity<?> response = orderController.getOrder("order-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getOrder_notFound_returns404() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        ResponseEntity<?> response = orderController.getOrder("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createOrder_setsDefaults_returnsCreated() {
        Order newOrder = new Order();
        newOrder.setTotalAmount(100);
        // createdAt=0 and status=null should be set by controller

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId("new-id");
            return o;
        });

        ResponseEntity<?> response = orderController.createOrder(newOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CREATED", newOrder.getStatus());
        assertTrue(newOrder.getCreatedAt() > 0);
    }

    @Test
    void createOrder_keepsExistingStatusAndTimestamp() {
        Order newOrder = new Order();
        newOrder.setStatus("PAID");
        newOrder.setCreatedAt(12345L);

        when(orderRepository.save(any())).thenReturn(newOrder);

        orderController.createOrder(newOrder);

        assertEquals("PAID", newOrder.getStatus());
        assertEquals(12345L, newOrder.getCreatedAt());
    }

    @Test
    void updateOrder_existing_returnsUpdated() {
        when(orderRepository.existsById("order-1")).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(testOrder);

        ResponseEntity<?> response = orderController.updateOrder("order-1", testOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateOrder_notFound_returns404() {
        when(orderRepository.existsById("missing")).thenReturn(false);

        ResponseEntity<?> response = orderController.updateOrder("missing", testOrder);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteOrder_existing_returnsOk() {
        when(orderRepository.existsById("order-1")).thenReturn(true);

        ResponseEntity<?> response = orderController.deleteOrder("order-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository).deleteById("order-1");
    }

    @Test
    void deleteOrder_notFound_returns404() {
        when(orderRepository.existsById("missing")).thenReturn(false);

        ResponseEntity<?> response = orderController.deleteOrder("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
