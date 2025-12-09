package com.nurularifins.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the home page and public pages.
 */
@Controller
public class HomeController {

    /**
     * Displays the home page.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
