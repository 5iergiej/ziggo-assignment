package com.example.restservice.model.order;

import com.example.restservice.model.order.exceptions.*;
import com.example.restservice.model.user.ReqresUserProvider;
import com.example.restservice.model.user.User;
import com.example.restservice.model.user.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {
    @MockBean
    private ReqresUserProvider reqresUserProvider;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void getById_orderExists_orderIsReturned() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order(12345L, "john.doe@example.com", "John", "Doe")));

        // when
        final var order = orderService.getById(1L);

        // then
        assertThat(order.getProductId()).isEqualTo(12345L);
        assertThat(order.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(order.getFirstName()).isEqualTo("John");
        assertThat(order.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getById_orderDoesNotExists_ExceptionIsThrown() {
        // given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class, () -> orderService.getById(1L));
    }

    @Test
    void getAll_orderDoesNotExists_ExceptionIsThrown() {
        // given
        final var order1 = new Order(123L, "john.doe@example.com", "John", "Doe");
        final var order2 = new Order(456L, "jane.doe@example.com", "Jane", "Doe");

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // when
        final var orders = orderService.getAll();

        // then
        assertThat(orders).hasSize(2).containsExactlyInAnyOrder(order1, order2);
    }

    @Test
    void save_orderIsValidAndSavedByRepository_savedOrderIsReturned() {
        // given
        final var order = new Order(123L, "john.doe@example.com", "John", "Doe");

        when(reqresUserProvider.getUserByEmail("john.doe@example.com")).thenReturn(
            Optional.of(new User("john.doe@example.com", "John", "Doe")));
        when(orderRepository.findAll()).thenReturn(List.of());
        when(orderRepository.save(order)).thenReturn(order);

        // when
        final var savedOrder = orderService.save(123L, "john.doe@example.com");

        // then
        assertThat(savedOrder).isEqualTo(order);
    }

    @Test
    void save_userDoesNotExist_exceptionIsThrown() {
        // given
        final var order = new Order(123L, "john.doe@example.com", "John", "Doe");

        when(reqresUserProvider.getUserByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(orderRepository.findAll()).thenReturn(List.of());
        when(orderRepository.save(order)).thenReturn(order);

        // when & then
        assertThrows(UserNotFoundException.class, () -> orderService.save(123L, "john.doe@example.com"));
    }

    @Test
    void save_productWasAlreadyOrderedByUser_exceptionIsThrown() {
        // given
        final var order = new Order(123L, "john.doe@example.com", "John", "Doe");

        when(reqresUserProvider.getUserByEmail("john.doe@example.com")).thenReturn(
            Optional.of(new User("john.doe@example.com", "John", "Doe")));
        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // when & then
        assertThrows(InvalidOrderException.class, () -> orderService.save(123L, "john.doe@example.com"));
    }

    @Test
    void save_repositoryThrowsException_exceptionIsThrown() {
        // given
        final var order = new Order(123L, "john.doe@example.com", "John", "Doe");

        when(reqresUserProvider.getUserByEmail("john.doe@example.com")).thenReturn(
            Optional.of(new User("john.doe@example.com", "John", "Doe")));
        when(orderRepository.findAll()).thenReturn(List.of());
        when(orderRepository.save(order)).thenThrow(new RuntimeException("Something went wrong"));

        // when & then
        assertThrows(OrderException.class, () -> orderService.save(123L, "john.doe@example.com"));
    }

}