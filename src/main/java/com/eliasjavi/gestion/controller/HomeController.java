package com.eliasjavi.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/dashboard"})
    public String mostrarDashboard(Model model) {
        // Añade aquí atributos dinámicos que necesites mostrar en el dashboard
        // Por ejemplo:
        // model.addAttribute("totalIngresos", 0);
        // model.addAttribute("totalGastos", 0);
        // model.addAttribute("balance", 0);
        return "base";
    }
}