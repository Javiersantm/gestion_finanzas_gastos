package com.eliasjavi.gestion.ingresos.infraestructura.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ingresos")
public class IngresosController {

    @GetMapping
    public List<String> listarIngresos() {
        return List.of("Alquiler", "Comida", "Transporte");
    }
}