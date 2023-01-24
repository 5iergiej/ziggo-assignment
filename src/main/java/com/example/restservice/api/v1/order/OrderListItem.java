package com.example.restservice.api.v1.order;

public record OrderListItem(Long orderId, Long productId, String email, String firstName, String lastName) {
}
