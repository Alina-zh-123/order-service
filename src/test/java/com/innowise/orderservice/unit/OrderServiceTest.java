package com.innowise.orderservice.unit;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.exception.OrderException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order1;
    private OrderDto orderDto1;
    private OrderResponseDto orderResponseDto1;
    private UserDto userDto1;
    private String email1;

    @BeforeEach
    void setUp() {
        email1 = "qwerty@gmail.com";

        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setEmail(email1);

        orderDto1 = new OrderDto();
        orderDto1.setStatus("PENDING");
        orderDto1.setTotalPrice(BigDecimal.valueOf(100.0));
        orderDto1.setOrderItems(new ArrayList<>());

        order1 = new Order();
        order1.setId(1L);
        order1.setUserId(1L);
        order1.setStatus("PENDING");
        order1.setTotalPrice(BigDecimal.valueOf(100.0));
        order1.setOrderItems(new ArrayList<>());

        orderResponseDto1 = new OrderResponseDto(orderDto1, userDto1);
    }

    @Test
    void createOrder_shouldCreateOrder() {
        when(userClient.getUserByEmail(email1)).thenReturn(userDto1);
        when(orderMapper.dtoToOrder(orderDto1)).thenReturn(order1);
        when(orderRepository.save(order1)).thenReturn(order1);
        when(orderMapper.orderToDto(order1)).thenReturn(orderDto1);

        OrderResponseDto result = orderService.createOrder(orderDto1, email1);
        assertEquals(orderResponseDto1, result);

        verify(userClient).getUserByEmail(email1);
        verify(orderMapper).dtoToOrder(orderDto1);
        verify(orderRepository).save(order1);
        verify(orderMapper).orderToDto(order1);
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(userClient.getUserById(1L)).thenReturn(userDto1);
        when(orderMapper.orderToDto(order1)).thenReturn(orderDto1);

        OrderResponseDto result = orderService.getOrderById(1L);
        assertEquals(orderResponseDto1, result);

        verify(orderRepository).findById(1L);
        verify(userClient).getUserById(1L);
        verify(orderMapper).orderToDto(order1);
    }

    @Test
    void getOrderById_shouldThrowOrderException() {
        when(orderRepository.findById(1234L)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderService.getOrderById(1234L);
        });

        assertEquals("Order is not found!", exception.getMessage());
        verify(orderRepository).findById(1234L);
    }

    @Test
    void getAllOrdersWithFilter_ShouldReturnPageOrderResponseDto() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        String status = "PENDING";
        Pageable pageable = PageRequest.of(0, 10);

        List<Order> orders = List.of(order1);
        Page<Order> pageOrders = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findAll(
                ArgumentMatchers.<Specification<Order>>any(),
                eq(pageable)
        )).thenReturn(pageOrders);
        when(userClient.getUserById(1L)).thenReturn(userDto1);
        when(orderMapper.orderToDto(order1)).thenReturn(orderDto1);

        Page<OrderResponseDto> result = orderService.getAllOrdersWithFilter(start, end, status, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(orderResponseDto1, result.getContent().get(0));
        verify(orderRepository.findAll(
                ArgumentMatchers.<Specification<Order>>any(),
                eq(pageable)
        ));
        verify(userClient).getUserById(1L);
    }

    @Test
    void getOrdersByUserId_shouldReturnListOrderResponseDto() {
        Long userId = 1L;
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order1));
        when(userClient.getUserById(userId)).thenReturn(userDto1);
        when(orderMapper.orderToDto(order1)).thenReturn(orderDto1);

        List<OrderResponseDto> result = orderService.getOrdersByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(orderResponseDto1, result.get(0));
        verify(orderRepository).findByUserId(userId);
        verify(userClient).getUserById(userId);
    }

    @Test
    void updateOrder_shouldThrowExceptionWhenNotFound() {
        Long orderId = 1234L;
        OrderDto updateDto = new OrderDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.updateOrder(orderId, updateDto));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void deleteOrderById_shouldDeleteOrder() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order1));
        doNothing().when(orderRepository).deleteById(orderId);

        orderService.deleteOrderById(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).deleteById(orderId);
    }
}
