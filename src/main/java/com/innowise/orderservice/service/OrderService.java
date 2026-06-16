package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderDto orderDto);
    OrderResponseDto getOrderById(Long orderId);
    Page<OrderResponseDto> getAllOrdersWithFilter(LocalDateTime start, LocalDateTime end, List<String> statuses, Pageable pageable);
    List<OrderResponseDto> getOrdersByUserId(Long userId);
    OrderResponseDto updateOrder(Long orderId, OrderDto orderDto);
    void deleteOrderById(Long orderId);
}
