package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderDto;
import com.example.onlinebookstore.dto.orderitem.OrderItemDto;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.User;
import java.util.List;

public interface OrderService {
    OrderDto createOrder(User user, CreateOrderRequestDto requestDto);

    List<OrderDto> getAll(Long userId);

    OrderDto update(Long orderId, Order.Status status);

    List<OrderItemDto> getAllItemsById(Long userId, Long orderId);

    OrderItemDto getByIdAndUserIdAndOrderId(Long itemId, Long orderId, Long userId);
}
