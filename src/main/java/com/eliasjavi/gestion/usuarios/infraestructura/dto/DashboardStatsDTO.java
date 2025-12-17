package com.eliasjavi.gestion.usuarios.infraestructura.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsDTO {
    private BigDecimal ingresosMes;
    private BigDecimal gastosMes;
    private BigDecimal balanceTotalUsuario;
}