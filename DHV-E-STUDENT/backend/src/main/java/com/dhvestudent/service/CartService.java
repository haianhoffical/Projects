package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CartService {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public CartItem addToCart(Long userId, CartItemRequest req) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(req.getProductId()).orElseThrow();

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, req.getProductId())
                .orElse(null);
        if (item != null) {
            item.setQuantity(item.getQuantity() + req.getQuantity());
        } else {
            item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(req.getQuantity())
                    .build();
        }
        return cartItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem updateQuantity(Long userId, Long productId, int delta) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));
        int newQty = item.getQuantity() + delta;
        if (newQty <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        item.setQuantity(newQty);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
    }

    @Transactional(readOnly = true)
    public long getCartCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }
}
