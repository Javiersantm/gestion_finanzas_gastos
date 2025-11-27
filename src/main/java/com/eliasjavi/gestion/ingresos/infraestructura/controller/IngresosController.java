package com.eliasjavi.gestion.ingresos.infraestructura.controller;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.service.IngresosService;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Quitamos el import de jakarta.validation.Valid, ya que no lo usaremos en el argumento
import java.net.URI;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
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
    public ResponseEntity<List<IngresosEntity>> listarIngresos() {
        List<IngresosEntity> ingresos = ingresosService.listarIngresos();
        return ResponseEntity.ok(ingresos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresosEntity> obtenerIngreso(@PathVariable Long id) {
        Optional<IngresosEntity> ingreso = ingresosService.findById(id);
        return ingreso.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    // 👈 Eliminamos @Valid
    public ResponseEntity<IngresosEntity> crearIngreso(@RequestBody IngresosEntity ingreso) {
        UserEntity usuario = userRepository.findByEmail("mi.email@prueba.com")
                .orElseThrow(() -> new RuntimeException("Error: Usuario de prueba 'mi.email@prueba.com' no encontrado."));

        ingreso.setUsuario(usuario);

        IngresosEntity creado = ingresosService.guardarIngreso(ingreso);

        return ResponseEntity.created(URI.create("/ingresos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    // 👈 Eliminamos @Valid
    public ResponseEntity<IngresosEntity> actualizarIngreso(@PathVariable Long id, @RequestBody IngresosEntity ingreso) {
        Optional<IngresosEntity> existente = ingresosService.findById(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();

        UserEntity usuario = userRepository.findByEmail("mi.email@prueba.com")
                .orElseThrow(() -> new RuntimeException("Error: Usuario de prueba 'mi.email@prueba.com' no encontrado."));

        ingreso.setUsuario(usuario);

        ingreso.setId(id);
        IngresosEntity actualizado = ingresosService.guardarIngreso(ingreso);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIngreso(@PathVariable Long id) {
        boolean existed = ingresosService.deleteById(id);
        return existed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}