package com.example.restservice.model.order;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long productId;
    private String email;
    private String firstName;
    private String lastName;

    public Order(
        @NonNull final Long productId,
        @NonNull final String email,
        @NonNull final String firstName,
        @NonNull final String lastName) {
        this.productId = productId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Order() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) && Objects.equals(productId,
            order.productId) && Objects.equals(email, order.email) && Objects.equals(firstName,
            order.firstName) && Objects.equals(lastName, order.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId, email, firstName, lastName);
    }
}
