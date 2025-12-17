package com.eliasjavi.gestion.ingresos.infraestructura.controller;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.service.IngresosService;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // IMPORTANTE
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingresos")
public class IngresosController {

    private final IngresosService ingresosService;
    private final UserRepository userRepository;

    public IngresosController(IngresosService ingresosService, UserRepository userRepository) {
        this.ingresosService = ingresosService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<IngresosEntity>> listarIngresos(Authentication authentication) {
        String email = authentication.getName();

        // Verificamos si tiene el rol ADMIN
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<IngresosEntity> ingresos = ingresosService.listarIngresos(email, esAdmin);
        return ResponseEntity.ok(ingresos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresosEntity> obtenerIngreso(@PathVariable Long id) {
        Optional<IngresosEntity> ingreso = ingresosService.findById(id);
        return ingreso.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<IngresosEntity> crearIngreso(@RequestBody IngresosEntity ingreso, Principal principal) {
        UserEntity usuario = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ingreso.setUsuario(usuario);

        IngresosEntity creado = ingresosService.guardarIngreso(ingreso);
        return ResponseEntity.created(URI.create("/ingresos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresosEntity> actualizarIngreso(@PathVariable Long id, @RequestBody IngresosEntity ingreso, Principal principal) {
        Optional<IngresosEntity> existente = ingresosService.findById(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();

        UserEntity usuario = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ingreso.setUsuario(usuario);
        ingreso.setId(id);

        IngresosEntity actualizado = ingresosService.guardarIngreso(ingreso);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarIngreso(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            boolean deleted = ingresosService.deleteById(id, email, esAdmin);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}