package com.example.QuattroTech.shop.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for home page and general navigation
 */
@Controller
public class HomeController {

    /**
     * GET / - Show home/landing page
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
