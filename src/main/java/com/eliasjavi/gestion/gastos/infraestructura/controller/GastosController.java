package com.eliasjavi.gestion.gastos.infraestructura.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/gastos")
public class GastosController {

    @GetMapping
    public List<String> listarGastos() {
        return List.of("Alquiler", "Comida", "Transporte");
    }
}