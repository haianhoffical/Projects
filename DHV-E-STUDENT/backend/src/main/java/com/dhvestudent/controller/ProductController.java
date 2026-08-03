package com.dhvestudent.controller;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Product.ConditionType cond = null;
        if (condition != null && !condition.isEmpty()) {
            try { cond = Product.ConditionType.valueOf(condition.toUpperCase()); } catch (Exception ignored) {}
        }
        return ResponseEntity.ok(productService.getProducts(categoryId, cond, search, sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @GetMapping("/secondhand")
    public ResponseEntity<?> getSecondhand() {
        return ResponseEntity.ok(ApiResponse.success(productService.getSecondhandProducts()));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestParam Long sellerId,
                                           @ModelAttribute ProductRequest req,
                                           @RequestParam(required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(ApiResponse.success("Tạo sản phẩm thành công",
                productService.createProduct(sellerId, req, images)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestParam Long sellerId) {
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.ok(ApiResponse.success("Xóa sản phẩm thành công", null));
    }
}
