package com.eliasjavi.gestion.gastos.infraestructura.controller;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.service.GastosService;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // IMPORTANTE
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gastos")
@RequiredArgsConstructor
public class GastosController {

    private final GastosService gastosService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<GastosEntity>> listarGastos(Authentication authentication) {
        String email = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(gastosService.listarGastos(email, esAdmin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastosEntity> obtenerGasto(@PathVariable Long id) {
        return gastosService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearGasto(@RequestBody GastosEntity gasto, Principal principal) {
        try {
            UserEntity usuario = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            gasto.setUsuario(usuario);
            GastosEntity creado = gastosService.guardarGasto(gasto);

            return ResponseEntity.created(URI.create("/gastos/" + creado.getId())).body(creado);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarGasto(@PathVariable Long id, @RequestBody GastosEntity gasto, Principal principal) {
        gasto.setId(id);
        try {
            Optional<GastosEntity> actualizadoOpt = gastosService.actualizarGastoYActualizarSaldo(id, gasto);
            return actualizadoOpt.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarGasto(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            boolean deleted = gastosService.deleteById(id, email, esAdmin);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}