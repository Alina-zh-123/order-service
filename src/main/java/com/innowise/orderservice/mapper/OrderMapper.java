package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.ItemDto;
import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

import java.io.Serializable;

@Mapper(componentModel = "spring")
public interface OrderMapper extends Serializable {
    Order dtoToOrder(OrderDto orderDto);
    OrderDto orderToDto(Order order);

    OrderItem dtoToOrderItem(OrderItemDto dto);
    OrderItemDto orderItemToDto(OrderItem entity);

    Item dtoToItem(ItemDto dto);
    ItemDto itemToDto(Item entity);
}
