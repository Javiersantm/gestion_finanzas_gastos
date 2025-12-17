package com.eliasjavi.gestion.bizum.infraestructura.controller;

import com.eliasjavi.gestion.bizum.domain.service.BizumService;
import com.eliasjavi.gestion.bizum.infraestructura.dto.EnviarBizumDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/bizum")
@RequiredArgsConstructor
public class BizumController {

    private final BizumService bizumService;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarBizum(@Valid @RequestBody EnviarBizumDTO bizumDTO, Principal principal) {
        try {
            // Pasamos el email del que está logueado (Principal)
            bizumService.enviarBizum(principal.getName(), bizumDTO);
            return ResponseEntity.ok("{\"message\": \"¡Bizum enviado exitosamente!\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}