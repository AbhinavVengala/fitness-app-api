package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    private String userId;
    private List<MenuItem> items;
    private double totalAmount;
    private String status; // CREATED, PAID, FAILED
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private long createdAt;
}
