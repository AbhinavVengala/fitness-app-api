package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Order;
import com.abhi.FitnessTracker.Repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String keyId;

    @Value("${razorpay.key.secret:secret_placeholder}")
    private String keySecret;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Order orderRequest) throws RazorpayException {
        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequestJson = new JSONObject();
            orderRequestJson.put("amount", (int) (orderRequest.getTotalAmount() * 100)); // Amount in paise
            orderRequestJson.put("currency", "INR");
            orderRequestJson.put("receipt", "txn_" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequestJson);

            orderRequest.setRazorpayOrderId(razorpayOrder.get("id"));
            orderRequest.setStatus("CREATED");
            orderRequest.setCreatedAt(System.currentTimeMillis());
            
            Order savedOrder = orderRepository.save(orderRequest);

            return ResponseEntity.ok(savedOrder);
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        
        Order order = orderRepository.findAll().stream()
                .filter(o -> orderId.equals(o.getRazorpayOrderId()))
                .findFirst()
                .orElse(null);

        if (order != null) {
            order.setStatus("PAID");
            order.setRazorpayPaymentId(paymentId);
            orderRepository.save(order);
            return ResponseEntity.ok(Map.of("status", "success"));
        }
        
        return ResponseEntity.badRequest().body("Order not found");
    }
}
