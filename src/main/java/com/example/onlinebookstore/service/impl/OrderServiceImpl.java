package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderDto;
import com.example.onlinebookstore.dto.orderitem.OrderItemDto;
import com.example.onlinebookstore.mapper.OrderItemMapper;
import com.example.onlinebookstore.mapper.OrderMapper;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.order.OrderRepository;
import com.example.onlinebookstore.repository.orderitem.OrderItemRepository;
import com.example.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.onlinebookstore.service.OrderService;
import com.example.onlinebookstore.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartService shoppingCartService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto createOrder(User user, CreateOrderRequestDto requestDto) {
        Order order = new Order();
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find the shopping cart by id " + user.getId()));
        List<OrderItem> orderItems = shoppingCart.getCartItems()
                .stream()
                .map(c -> convertToOrderItem(c, order))
                .toList();
        order.setOrderItems(orderItems);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);

        BigDecimal total = orderItems.stream()
                .map(o -> o.getBook().getPrice().multiply(BigDecimal.valueOf(o.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        order.setShippingAddress(requestDto.getShippingAddress());
        Order saved = orderRepository.save(order);
        shoppingCartService.cleanShoppingCart(shoppingCart.getId());
        return orderMapper.toDto(saved);
    }

    @Override
    public List<OrderDto> getAll(Long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto update(Long orderId, Order.Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find an order by ID" + orderId));
        order.setStatus(status);
        System.out.println(order);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemDto> getAllItemsById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find an order by id " + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getByIdAndUserIdAndOrderId(Long itemId, Long orderId, Long userId) {
        return orderItemMapper.toDto(
                orderItemRepository.getOrderItemByIdAndOrder_IdAndOrder_User_Id(
                        itemId,
                        orderId,
                        userId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't find an order item by id " + itemId)));
    }

    private OrderItem convertToOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrder(order);
        return orderItem;
    }
}
