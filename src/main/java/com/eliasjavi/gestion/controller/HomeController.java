package com.eliasjavi.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/ingresos", "/"})
    public String mostrarIngresos(Model model) {
//        model.addAttribute("contenido", "partes/ingresos :: ingresosContent");
        return "base";
    }
}