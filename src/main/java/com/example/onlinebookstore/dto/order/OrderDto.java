package com.example.onlinebookstore.dto.order;

import com.example.onlinebookstore.dto.orderitem.OrderItemDto;
import com.example.onlinebookstore.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Order.Status status;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private List<OrderItemDto> orderItems;
}
