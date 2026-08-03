package com.dhvestudent.controller;

import com.dhvestudent.dto.*;
import com.dhvestudent.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam Long buyerId, @Valid @RequestBody OrderRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Tạo đơn hàng thành công",
                orderService.createOrder(buyerId, req)));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userId)));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(page, size)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateOrderStatus(id,
                com.dhvestudent.entity.Order.OrderStatus.valueOf(status.toUpperCase()))));
    }
}
