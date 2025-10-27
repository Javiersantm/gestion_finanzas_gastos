package com.eliasjavi.gestion.bizum.infraestructura.controller;
import com.eliasjavi.gestion.bizum.domain.service.BizumService;
import com.eliasjavi.gestion.bizum.infraestructura.dto.EnviarBizumDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bizum")
@RequiredArgsConstructor
public class BizumController {

    private final BizumService bizumService;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarBizum(@Valid @RequestBody EnviarBizumDTO bizumDTO) {

        try {
            // Llama al servicio de Bizum
            bizumService.enviarBizum(bizumDTO);

            return ResponseEntity.ok("¡Bizum enviado exitosamente!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}