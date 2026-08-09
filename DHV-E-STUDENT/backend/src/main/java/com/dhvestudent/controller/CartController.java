package com.dhvestudent.controller;

import com.dhvestudent.dto.*;
import com.dhvestudent.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestParam Long userId, @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addToCart(userId, req)));
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartItems(userId)));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateQty(@RequestParam Long userId, @PathVariable Long productId,
                                        @RequestParam int delta) {
        return ResponseEntity.ok(ApiResponse.success(cartService.updateQuantity(userId, productId, delta)));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> remove(@RequestParam Long userId, @PathVariable Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa khỏi giỏ hàng", null));
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCount(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartCount(userId)));
    }
}
