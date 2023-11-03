package com.example.onlinebookstore.repository.orderitem;

import com.example.onlinebookstore.model.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> getOrderItemByIdAndOrder_IdAndOrder_User_Id(
            Long itemId, Long orderId, Long userId);
}
