package com.example.restservice.model.order.exceptions;

public class InvalidOrderException extends OrderException {
    public InvalidOrderException(String message) {
        super(message);
    }
}
