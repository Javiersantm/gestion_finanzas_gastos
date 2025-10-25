package com.eliasjavi.gestion.ingresos.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "ingresos")
public class IngresosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuario_id;
    private String fuente;
    private Double cantidad;
    private LocalDate fecha;
}
