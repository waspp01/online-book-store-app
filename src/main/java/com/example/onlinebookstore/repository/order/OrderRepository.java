package com.example.onlinebookstore.repository.order;

import com.example.onlinebookstore.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserId(Long oderId, Long userId);
}
