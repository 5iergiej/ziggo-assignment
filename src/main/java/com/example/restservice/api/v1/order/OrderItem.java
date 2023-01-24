package com.example.restservice.api.v1.order;

public record OrderItem(Long orderId, Long productId, String email, String firstName, String lastName) {
}

