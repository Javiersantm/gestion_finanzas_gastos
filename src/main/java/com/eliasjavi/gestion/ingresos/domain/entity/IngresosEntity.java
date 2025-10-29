package com.eliasjavi.gestion.ingresos.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "ingresos")
public class IngresosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    private String fuente;

    private BigDecimal cantidad;

    private LocalDate fecha;
}