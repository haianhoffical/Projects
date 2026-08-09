package com.dhvestudent.service;

import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;

    @Value("${momo.partner-code}") private String momoPartnerCode;
    @Value("${momo.access-key}") private String momoAccessKey;
    @Value("${momo.secret-key}") private String momoSecretKey;
    @Value("${momo.return-url}") private String momoReturnUrl;

    @Value("${zalopay.app-id}") private String zaloAppId;
    @Value("${zalopay.key1}") private String zaloKey1;
    @Value("${zalopay.return-url}") private String zaloReturnUrl;

    @Transactional
    public Map<String, String> createMomoPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toan DHV E-STUDENT #" + orderId;
        String amount = order.getTotalAmount().toBigInteger().toString();

        Map<String, String> result = new HashMap<>();
        result.put("partnerCode", momoPartnerCode);
        result.put("accessKey", momoAccessKey);
        result.put("requestId", requestId);
        result.put("amount", amount);
        result.put("orderId", orderId.toString());
        result.put("orderInfo", orderInfo);
        result.put("returnUrl", momoReturnUrl);
        result.put("notifyUrl", "http://localhost:8080/payment/momo-notify");
        result.put("requestType", "captureMoMoWallet");
        result.put("signature", "DEMO_SIGNATURE");
        result.put("payUrl", "https://test-payment.momo.vn/gw_payment/payment/qr?partnerCode=" + momoPartnerCode + "&orderId=" + orderId);
        return result;
    }

    @Transactional
    public Map<String, String> createZaloPayPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Map<String, String> result = new HashMap<>();
        result.put("app_id", zaloAppId);
        result.put("app_trans_id", "DHV" + System.currentTimeMillis());
        result.put("app_user", "user" + order.getBuyer().getId());
        result.put("app_time", String.valueOf(System.currentTimeMillis()));
        result.put("amount", order.getTotalAmount().toBigInteger().toString());
        result.put("item", "[]");
        result.put("description", "Thanh toan DHV E-STUDENT #" + orderId);
        result.put("bank_code", "");
        result.put("embed_data", "{\"redirecturl\":\"" + zaloReturnUrl + "\"}");
        result.put("callback_url", "http://localhost:8080/payment/zalopay-callback");
        result.put("mac", "DEMO_MAC");
        result.put("order_url", "https://sandbox.zalopay.com.vn/payment?order=" + orderId);
        return result;
    }

    @Transactional
    public Payment processPaymentCallback(String transactionId, boolean success) {
        Payment payment = paymentRepository.findByTransactionId(transactionId).orElse(null);
        if (payment == null) return null;
        payment.setStatus(success ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED);
        if (success) {
            payment.setPaidAt(LocalDateTime.now());
            Order order = payment.getOrder();
            order.setStatus(Order.OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }
        return paymentRepository.save(payment);
    }
}
