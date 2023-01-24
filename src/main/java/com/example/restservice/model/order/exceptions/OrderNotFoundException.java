package com.example.restservice.model.order.exceptions;

public class OrderNotFoundException extends OrderException {
    public OrderNotFoundException(Long id) {
        super(String.format("Order with id `%d` not found", id));
    }
}
