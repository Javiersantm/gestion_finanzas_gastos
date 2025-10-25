package com.eliasjavi.gestion.gastos.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "gastos")
public class GastosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuario_id;
    private String descripcion;
    private Double cantidad;
    private LocalDate fecha;
}
