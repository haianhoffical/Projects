package com.dhvestudent.controller;

import com.dhvestudent.dto.ApiResponse;
import com.dhvestudent.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/momo/{orderId}")
    public ResponseEntity<?> createMomo(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createMomoPayment(orderId)));
    }

    @PostMapping("/zalopay/{orderId}")
    public ResponseEntity<?> createZaloPay(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createZaloPayPayment(orderId)));
    }

    @GetMapping("/momo-return")
    public ResponseEntity<?> momoReturn(@RequestParam String transactionId,
                                         @RequestParam(defaultValue = "0") int resultCode) {
        paymentService.processPaymentCallback(transactionId, resultCode == 0);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thanh toán thành công", null));
    }

    @GetMapping("/zalopay-return")
    public ResponseEntity<?> zaloReturn(@RequestParam String transactionId,
                                         @RequestParam(defaultValue = "0") int status) {
        paymentService.processPaymentCallback(transactionId, status == 1);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thanh toán thành công", null));
    }
}
