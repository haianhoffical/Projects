package com.dhvestudent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/index", "/home"})
    public String home() { return "index"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @GetMapping("/products")
    public String products() { return "products"; }

    @GetMapping("/tutors")
    public String tutors() { return "tutors"; }

    @GetMapping("/jobs")
    public String jobs() { return "jobs"; }

    @GetMapping("/forum")
    public String forum() { return "forum"; }

    @GetMapping("/cart")
    public String cart() { return "cart"; }

    @GetMapping("/profile")
    public String profile() { return "profile"; }

    @GetMapping("/admin")
    public String admin() { return "admin/dashboard"; }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() { return "admin/dashboard"; }

    @GetMapping("/admin/users")
    public String adminUsers() { return "admin/users"; }

    @GetMapping("/admin/products")
    public String adminProducts() { return "admin/products"; }

    @GetMapping("/admin/orders")
    public String adminOrders() { return "admin/orders"; }
}
