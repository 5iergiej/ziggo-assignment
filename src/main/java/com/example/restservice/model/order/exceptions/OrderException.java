package com.example.restservice.model.order.exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}
