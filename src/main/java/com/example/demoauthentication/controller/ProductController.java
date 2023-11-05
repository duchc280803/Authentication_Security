package com.example.demoauthentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product/")
public class ProductController {

    @GetMapping("banana")
    public String banana() {
        return "Đây là quả chuối";
    }

    @GetMapping("apple")
    public String apple() {
        return "Đây là quả táo";
    }

    @GetMapping("fish")
    public String fish() {
        return "Đây là con cá";
    }

    @GetMapping("sion")
    public String sion() {
        return "Đây là con sư tử";
    }

}
