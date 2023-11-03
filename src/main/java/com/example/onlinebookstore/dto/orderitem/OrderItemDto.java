package com.example.onlinebookstore.dto.orderitem;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private Integer quantity;
    private BigDecimal price;
}
