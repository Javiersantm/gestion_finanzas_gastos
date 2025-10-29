package com.eliasjavi.gestion.ingresos.infraestructura.controller;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.service.IngresosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ingresos")
public class IngresosController {

    private final IngresosService ingresosService;

    public IngresosController(IngresosService ingresosService) {
        this.ingresosService = ingresosService;
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
    public ResponseEntity<IngresosEntity> crearIngreso(@Valid @RequestBody IngresosEntity ingreso) {
        IngresosEntity creado = ingresosService.guardarIngreso(ingreso);
        return ResponseEntity.created(URI.create("/ingresos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresosEntity> actualizarIngreso(@PathVariable Long id, @Valid @RequestBody IngresosEntity ingreso) {
        Optional<IngresosEntity> existente = ingresosService.findById(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();
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
