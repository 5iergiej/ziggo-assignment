package com.example.restservice.api.v1.order;

import com.example.restservice.model.order.Order;
import com.example.restservice.model.order.OrderService;
import com.example.restservice.model.order.exceptions.InvalidOrderException;
import com.example.restservice.model.order.exceptions.OrderNotFoundException;
import com.example.restservice.model.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class OrderResource {

    private final OrderService orderService;

    @Autowired
    OrderResource(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderItem> getOrderById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(toDto(orderService.getById(id)));
        } catch (OrderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/order")
    public ResponseEntity<OrderList> listOrders() {
        return ResponseEntity.ok().body(toDto(orderService.getAll()));
    }

    @PostMapping("/order")
    public ResponseEntity<Long> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        try {
            final var savedOrder = orderService.save(createOrderRequest.productId(), createOrderRequest.email());

            return ResponseEntity.ok().body(savedOrder.getOrderId());
        } catch (UserNotFoundException | InvalidOrderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private OrderList toDto(List<Order> orders) {
        return new OrderList(orders.stream().map(order ->
            new OrderListItem(
                order.getOrderId(),
                order.getProductId(),
                order.getEmail(),
                order.getFirstName(),
                order.getLastName()
            )
        ).toList());
    }

    private OrderItem toDto(Order order) {
        return new OrderItem(
            order.getOrderId(),
            order.getProductId(),
            order.getEmail(),
            order.getFirstName(),
            order.getLastName()
        );
    }
}
