package com.eliasjavi.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping({"/admin"})
    public String admin() {
        return "base";
    }
}