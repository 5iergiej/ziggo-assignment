package com.example.restservice.model.order;

import com.example.restservice.model.order.exceptions.*;
import com.example.restservice.model.user.ReqresUserProvider;
import com.example.restservice.model.user.User;
import com.example.restservice.model.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ReqresUserProvider userProvider;

    @Autowired
    OrderService(final OrderRepository orderRepository, final ReqresUserProvider userProvider) {
        this.orderRepository = orderRepository;
        this.userProvider = userProvider;
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order save(Long productId, String email) {
        final var user = userProvider.getUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));

        ensureProductWasNotOrderedByCustomerBefore(productId, user);

        final var newOrder = new Order(productId, user.email(), user.firstName(), user.lastName());

        try {
            return orderRepository.save(newOrder);
        } catch (Exception e) {
            throw new OrderException(String.format("Failed to save the order. ProductID: `%s`, user email: `%s`", productId, email));
        }
    }

    private void ensureProductWasNotOrderedByCustomerBefore(Long productId, User user) {
        final var existingOrders = orderRepository.findAll(
            where(hasUser(user)).and(hasProduct(productId))
        );

        if (!existingOrders.isEmpty()) {
            throw new InvalidOrderException(
                String.format("Product with id `%d` was already ordered by customer `%s`", productId, user.email())
            );
        }
    }

    private static Specification<Order> hasUser(User user) {
        return (order, cq, cb) -> cb.equal(order.get("email"), user.email());
    }

    private static Specification<Order> hasProduct(Long productId) {
        return (order, cq, cb) -> cb.equal(order.get("productId"), productId);
    }
}
