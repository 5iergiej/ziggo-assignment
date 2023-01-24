package com.example.restservice.api.v1.order;

import com.example.restservice.model.order.Order;
import com.example.restservice.model.order.*;
import com.example.restservice.model.user.ReqresUserProvider;
import com.example.restservice.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class OrderResourceTest {

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private ReqresUserProvider reqresUserProvider;

    @Autowired
    private MockMvc mockMvc;

    private JacksonTester<OrderItem> orderItemJson;
    private JacksonTester<OrderList> orderListJson;

    private final static String EXISTING_USER_EMAIL = "john.doe@example.com";
    private final static String NON_EXISTING_USER_EMAIL = "unknown.person@example.com";

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());

        when(reqresUserProvider.getUserByEmail(EXISTING_USER_EMAIL)).thenReturn(Optional.of(new User(EXISTING_USER_EMAIL, "John", "Doe")));
        when(reqresUserProvider.getUserByEmail(NON_EXISTING_USER_EMAIL)).thenReturn(Optional.empty());
    }

    @AfterEach
    public void cleanup() {
        orderRepository.deleteAll();
    }

    @Test
    void getOrderById_orderExists_orderIsReturned() throws Exception {
        // given
        final var savedOrder = orderRepository.save(prepareValidOrder());

        // when
        final var response = mockMvc.perform(MockMvcRequestBuilders.get("/order/" + savedOrder.getOrderId())
            .contentType("application/json")).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(orderItemJson.write(toOrderItem(savedOrder)).getJson());
    }

    @Test
    void getOrderById_orderDoesNotExist_NotFoundStatusIsReturned() throws Exception {
        // given
        // when
        final var response = mockMvc.perform(MockMvcRequestBuilders.get("/order/999")
            .contentType("application/json")).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void listOrders_multipleOrdersExist_allOrdersAreReturned() throws Exception {
        // given
        final var order1 = orderRepository.save(prepareValidOrder(123L));
        final var order2 = orderRepository.save(prepareValidOrder(456L));

        // when
        final var response = mockMvc.perform(MockMvcRequestBuilders.get("/order")
            .contentType("application/json")).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(
            orderListJson.write(
                new OrderList(Stream.of(order1, order2)
                    .map(this::toOrderListItem)
                    .collect(Collectors.toList())
                )
            ).getJson());
    }

    @Test
    void createOrder_orderIsValid_orderIsCreatedAndIdIsReturned() throws Exception {
        // given
        final var createOrderPaylaod = """
            {
                 "productId": "12345",
                 "email": "john.doe@example.com"
             }
            """;

        // when
        final var response = mockMvc.perform(MockMvcRequestBuilders.post("/order")
            .content(createOrderPaylaod).contentType("application/json")).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        final var savedOrders = orderRepository.findAll();
        assertThat(savedOrders).hasSize(1);

        final var savedOrder = savedOrders.stream().findFirst().get();
        assertThat(savedOrder.getProductId()).isEqualTo(12345L);
        assertThat(savedOrder.getEmail()).isEqualTo(EXISTING_USER_EMAIL);
        assertThat(response.getContentAsString()).isEqualTo(savedOrder.getOrderId().toString());
    }

    @Test
    void createOrder_UserDoesNotExist_orderIsNotCreatedAndBadRequestStatusIsReturned() throws Exception {
        // given
        final var createOrderPaylaod = """
            {
                 "productId": "12345",
                 "email": "unknown.person@example.com"
             }
            """;

        // when
        final var response = mockMvc.perform(MockMvcRequestBuilders.post("/order")
            .content(createOrderPaylaod).contentType("application/json")).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createOrder_customerAlreadyOrderedTheProduct_orderIsNotCreatedAndBadRequestStatusIsReturned() throws Exception {
        // given
        orderRepository.save(prepareValidOrder(12345L));

        final var createOrderPaylaod = """
            {
                 "productId": "12345",
                 "email": "john.doe@example.com"
             }
            """;

        // when
        final var response = mockMvc.perform(MockMvcRequestBuilders.post("/order")
            .content(createOrderPaylaod).contentType("application/json")).andReturn().getResponse();

        // then
        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Order prepareValidOrder() {
        return new Order(12345L, EXISTING_USER_EMAIL, "John", "Doe");
    }

    private Order prepareValidOrder(Long productId) {
        return new Order(productId, EXISTING_USER_EMAIL, "John", "Doe");
    }

    private OrderItem toOrderItem(Order order) {
        return new OrderItem(
            order.getOrderId(),
            order.getProductId(),
            order.getEmail(),
            order.getFirstName(),
            order.getLastName()
        );
    }

    private OrderListItem toOrderListItem(Order order) {
        return new OrderListItem(
            order.getOrderId(),
            order.getProductId(),
            order.getEmail(),
            order.getFirstName(),
            order.getLastName()
        );
    }
}