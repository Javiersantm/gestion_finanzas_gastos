package com.eliasjavi.gestion.gastos.infraestructura.controller;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.service.GastosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gastos")
public class GastosController {

    private final GastosService gastosService;

    public GastosController(GastosService gastosService) {
        this.gastosService = gastosService;
    }

    @GetMapping
    public ResponseEntity<List<GastosEntity>> listarGastos() {
        List<GastosEntity> gastos = gastosService.listarGastos();
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastosEntity> obtenerGasto(@PathVariable Long id) {
        Optional<GastosEntity> gasto = gastosService.findById(id);
        return gasto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GastosEntity> crearGasto(@Valid @RequestBody GastosEntity gasto) {
        GastosEntity creado = gastosService.guardarGasto(gasto);
        return ResponseEntity.created(URI.create("/gastos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastosEntity> actualizarGasto(@PathVariable Long id, @Valid @RequestBody GastosEntity gasto) {
        Optional<GastosEntity> existente = gastosService.findById(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();
        gasto.setId(id);
        GastosEntity actualizado = gastosService.guardarGasto(gasto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        boolean existed = gastosService.deleteById(id);
        return existed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
