package com.dhvestudent.service;

import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private TutorRepository tutorRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private ForumPostRepository forumPostRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countByIsActiveTrue());
        stats.put("totalProducts", productRepository.countActiveProducts());
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        stats.put("totalTutors", tutorRepository.countByIsActiveTrue());
        stats.put("totalJobs", jobRepository.countByActiveTrue());
        stats.put("totalForumPosts", forumPostRepository.count());
        return stats;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }
}
