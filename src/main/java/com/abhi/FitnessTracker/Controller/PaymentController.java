package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Order;
import com.abhi.FitnessTracker.Repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final OrderRepository orderRepository;

    public PaymentController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Order orderRequest) {
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
            logger.info("Order created: {}", savedOrder.getRazorpayOrderId());

            return ResponseEntity.ok(savedOrder);
        } catch (RazorpayException e) {
            logger.error("Failed to create Razorpay order", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Payment initialization failed. Please try again."));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");

            if (orderId == null || paymentId == null || signature == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing payment verification fields"));
            }

            // Verify Razorpay signature using HMAC-SHA256
            JSONObject verifyData = new JSONObject();
            verifyData.put("razorpay_order_id", orderId);
            verifyData.put("razorpay_payment_id", paymentId);
            verifyData.put("razorpay_signature", signature);

            boolean isValid = Utils.verifyPaymentSignature(verifyData, keySecret);

            if (!isValid) {
                logger.warn("Payment signature verification failed for order: {}", orderId);
                return ResponseEntity.badRequest().body(Map.of("error", "Payment verification failed"));
            }

            Order order = orderRepository.findByRazorpayOrderId(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));
            }

            order.setStatus("PAID");
            order.setRazorpayPaymentId(paymentId);
            orderRepository.save(order);

            logger.info("Payment verified successfully for order: {}", orderId);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (RazorpayException e) {
            logger.error("Payment verification error", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Payment verification failed"));
        }
    }

    /**
     * Test payment endpoints — only available in dev profile.
     * These bypass Razorpay for local development/testing.
     */
    @Profile("dev")
    @RestController
    @RequestMapping("/api/payment/test")
    static class TestPaymentController {

        private final OrderRepository orderRepository;

        TestPaymentController(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }

        @PostMapping("/create-order")
        public ResponseEntity<?> createTestOrder(@RequestBody Order orderRequest) {
            String testOrderId = "test_order_" + System.currentTimeMillis();

            orderRequest.setRazorpayOrderId(testOrderId);
            orderRequest.setStatus("CREATED");
            orderRequest.setCreatedAt(System.currentTimeMillis());

            Order savedOrder = orderRepository.save(orderRequest);
            return ResponseEntity.ok(savedOrder);
        }

        @PostMapping("/verify")
        public ResponseEntity<?> verifyTestPayment(@RequestBody Map<String, String> data) {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");

            if (paymentId == null) {
                paymentId = "test_pay_" + System.currentTimeMillis();
            }

            Order order = orderRepository.findByRazorpayOrderId(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));
            }

            order.setStatus("PAID");
            order.setRazorpayPaymentId(paymentId);
            orderRepository.save(order);
            return ResponseEntity.ok(Map.of("status", "success", "paymentId", paymentId));
        }
    }
}
