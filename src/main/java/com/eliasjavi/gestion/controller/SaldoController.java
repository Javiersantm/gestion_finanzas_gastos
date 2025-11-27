package com.eliasjavi.gestion.controller;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController // Indica que este controlador devuelve JSON/datos
@RequestMapping("/api") // Prefijo base para la API
@RequiredArgsConstructor
public class SaldoController {

    private final UserRepository userRepository;

    /**
     * Endpoint para obtener el saldo actual del usuario de prueba en formato JSON.
     * URL: /api/saldo-actual
     */
    @GetMapping("/saldo-actual")
    public BigDecimal obtenerSaldoActual() {
        // Buscar el usuario de prueba
        UserEntity usuario = userRepository.findByEmail("mi.email@prueba.com")
                .orElseThrow(() -> new RuntimeException("Error: Usuario de prueba 'mi.email@prueba.com' no encontrado en la BBDD."));

        // Devolver solo el valor BigDecimal del saldo
        return usuario.getSaldo();
    }
}