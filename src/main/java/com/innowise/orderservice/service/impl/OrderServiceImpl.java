package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.UserDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.exception.OrderException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repositoty.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;

    @Override
    public OrderResponseDto createOrder(OrderDto orderDto, String email) {
        UserDto userDto = userClient.getUserByEmail(email);

        Order order = orderMapper.dtoToOrder(orderDto);
        order.setUserId(userDto.getId());

        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }

        Order res = orderRepository.save(order);
        return new OrderResponseDto(orderMapper.orderToDto(res), userDto);
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order is not found!"));

        UserDto userDto = userClient.getUserById(order.getUserId());
        return new OrderResponseDto(orderMapper.orderToDto(order), userDto);
    }

    @Override
    public Page<OrderResponseDto> getAllOrdersWithFilter(LocalDateTime start, LocalDateTime end, String status, Pageable pageable) {
        Specification<Order> spec = Specification
                .where(OrderSpecification.createdBetween(start, end))
                .and(OrderSpecification.hasStatus(status));

        Page<Order> res = orderRepository.findAll(spec, pageable);

        return res.map(order -> {
            UserDto userDto = userClient.getUserById(order.getUserId());
            return new OrderResponseDto(orderMapper.orderToDto(order), userDto);
        });
    }

    @Override
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        List<Order> res = orderRepository.findByUserId(userId);
        UserDto userDto = userClient.getUserById(userId);
        return res.stream()
                .map(order -> new OrderResponseDto(orderMapper.orderToDto(order), userDto))
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order is not found!"));

        order.setStatus(orderDto.getStatus());
        order.setTotalPrice(orderDto.getTotalPrice());

        order.getOrderItems().clear();

        if (orderDto.getOrderItems() != null) {
            orderDto.getOrderItems().forEach(itemDto -> {
                OrderItem orderItem = orderMapper.dtoToOrderItem(itemDto);
                orderItem.setOrder(order);
                order.getOrderItems().add(orderItem);
            });
        }

        Order updatedOrder = orderRepository.save(order);
        UserDto userDto = userClient.getUserById(updatedOrder.getUserId());

        return new OrderResponseDto(orderMapper.orderToDto(updatedOrder), userDto);
    }

    @Override
    @Transactional
    public void deleteOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order is not found!"));

        orderRepository.deleteById(id);
    }
}
