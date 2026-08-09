package com.dhvestudent.controller;

import com.dhvestudent.dto.*;
import com.dhvestudent.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @GetMapping
    public ResponseEntity<?> getTutors(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "rating") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(tutorService.getTutors(subject, maxPrice, search, sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTutor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tutorService.getTutorById(id)));
    }

    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(ApiResponse.success(tutorService.countActiveTutors()));
    }
}
