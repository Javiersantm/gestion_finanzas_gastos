package com.eliasjavi.gestion.controller;

import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SaldoController {

    private final UserRepository userRepository;

    /**
     * Endpoint para obtener el saldo TOTAL DE LA CASA (Suma de todos los usuarios).
     * URL: /api/saldo-actual
     */
    @GetMapping("/saldo-actual")
    public BigDecimal obtenerSaldoTotalCasa() {
        // Llamamos al nuevo método del repositorio que suma todo
        return userRepository.sumarSaldosDeTodos();
    }
}