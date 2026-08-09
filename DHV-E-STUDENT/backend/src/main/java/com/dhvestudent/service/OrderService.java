package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Transactional
    public Order createOrder(Long buyerId, OrderRequest req) {
        User buyer = userRepository.findById(buyerId).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByUserId(buyerId);
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống");

        BigDecimal total = cartItems.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .buyer(buyer)
                .totalAmount(total)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(req.getShippingAddress())
                .shippingPhone(req.getShippingPhone())
                .note(req.getNote())
                .build();
        orderRepository.save(order);

        for (CartItem ci : cartItems) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getProduct().getPrice())
                    .build();
            orderItemRepository.save(oi);
            order.getItems().add(oi);
        }

        Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(req.getPaymentMethod().toUpperCase());
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(method)
                .amount(total)
                .status(Payment.PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        order.setPayment(payment);

        cartItemRepository.deleteAll(cartItems);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(int page, int size) {
        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
