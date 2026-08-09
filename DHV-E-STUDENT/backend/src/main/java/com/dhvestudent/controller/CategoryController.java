package com.dhvestudent.controller;

import com.dhvestudent.dto.ApiResponse;
import com.dhvestudent.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc()));
    }
}
