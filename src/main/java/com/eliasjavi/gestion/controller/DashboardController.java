package com.eliasjavi.gestion.controller;

import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import com.eliasjavi.gestion.usuarios.infraestructura.dto.DashboardStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IngresosRepository ingresosRepository;
    private final GastosRepository gastosRepository;
    private final UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> obtenerEstadisticas(Principal principal) {
        String email = principal.getName();

        // Calcular fechas del mes actual
        LocalDate now = LocalDate.now();
        LocalDate inicioMes = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = now.with(TemporalAdjusters.lastDayOfMonth());

        // 1. Total Ingresos Mes -> GLOBAL (DE TODA LA CASA)
        // Usamos el método sumarIngresosGlobales (sin filtrar por usuario)
        var totalIngresosCasa = ingresosRepository.sumarIngresosGlobales(inicioMes, finMes);

        // 2. Total Gastos Mes -> GLOBAL (DE TODA LA CASA)
        // Usamos el método sumarGastosGlobales (sin filtrar por usuario)
        var totalGastosCasa = gastosRepository.sumarGastosGlobales(inicioMes, finMes);

        // 3. Balance Actual -> PERSONAL (Tu saldo disponible)
        UserEntity usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(DashboardStatsDTO.builder()
                .ingresosMes(totalIngresosCasa)
                .gastosMes(totalGastosCasa)
                .balanceTotalUsuario(usuario.getSaldo())
                .build());
    }

    @GetMapping("/chart-data")
    public ResponseEntity<Map<String, Object>> obtenerDatosGrafico() {
        LocalDate now = LocalDate.now();
        LocalDate inicioMes = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = now.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> resultados = gastosRepository.sumarGastosPorCategoria(inicioMes, finMes);

        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        for (Object[] fila : resultados) {
            String categoria = (String) fila[0];
            BigDecimal cantidad = (BigDecimal) fila[1];

            // Si la categoría es nula (datos viejos), poner 'Otros'
            if (categoria == null || categoria.trim().isEmpty()) {
                categoria = "Otros";
            }

            labels.add(categoria);
            data.add(cantidad);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}