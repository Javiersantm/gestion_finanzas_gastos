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
        // Se llama a guardarGasto, que resta la cantidad al saldo.
        GastosEntity creado = gastosService.guardarGasto(gasto);
        return ResponseEntity.created(URI.create("/gastos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastosEntity> actualizarGasto(@PathVariable Long id, @Valid @RequestBody GastosEntity gasto) {
        gasto.setId(id);

        // ** CAMBIO CLAVE: Llamamos al nuevo método del servicio que maneja la lógica de reversión de saldo. **
        Optional<GastosEntity> actualizadoOpt = gastosService.actualizarGastoYActualizarSaldo(id, gasto);

        return actualizadoOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        // Se llama a deleteById, que revierte (suma) la cantidad al saldo.
        boolean existed = gastosService.deleteById(id);
        return existed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
