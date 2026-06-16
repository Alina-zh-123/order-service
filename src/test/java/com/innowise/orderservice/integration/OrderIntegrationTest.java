package com.innowise.orderservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class OrderIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    private static WireMockServer wireMockServer;

    private String email1;
    private UserDto userDto1;
    private OrderDto orderDto1;
    private Order savedOrder;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);

        registry.add("USER_SERVICE_URL", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        wireMockServer.resetAll();

        email1 = "qwerty@gmail.com";

        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setEmail(email1);

        orderDto1 = new OrderDto();
        orderDto1.setStatus("PENDING");
        orderDto1.setTotalPrice(BigDecimal.valueOf(100.0));
        orderDto1.setOrderItems(new ArrayList<>());

        Order order = new Order();
        order.setUserId(1L);
        order.setStatus("PENDING");
        order.setTotalPrice(BigDecimal.valueOf(100.0));
        order.setOrderItems(new ArrayList<>());
        savedOrder = orderRepository.save(order);
    }

    @Test
    void createOrder_shouldCreateOrderAndReturnResponse() throws Exception {
        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(email1))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(userDto1))
                        .withStatus(200)));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/")
                        .param("email", email1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto1)))
                .andExpect(status().isCreated());
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {
        stubFor(get(urlEqualTo("/users/" + savedOrder.getUserId()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(userDto1))
                        .withStatus(200)));

        mockMvc.perform(get("/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.totalPrice", is(100.0)))
                .andExpect(jsonPath("$.user.id", is(1)));
    }

    @Test
    void getOrderById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/orders/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Order is not found!")));
    }

    @Test
    void deleteOrder_shouldDeleteFromDatabase() throws Exception {
        Long idToDelete = savedOrder.getId();

        assertTrue(orderRepository.findById(idToDelete).isPresent());

        mockMvc.perform(delete("/orders/{id}", idToDelete))
                .andExpect(status().isNoContent());

        assertFalse(orderRepository.findById(idToDelete).isPresent());
    }
}