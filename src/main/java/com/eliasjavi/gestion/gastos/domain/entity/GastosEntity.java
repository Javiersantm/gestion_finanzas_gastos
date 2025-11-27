package com.eliasjavi.gestion.gastos.domain.entity;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal; // <-- ¡IMPORTACIÓN NECESARIA!

@Entity
@Data
@Table(name = "gastos")
public class GastosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UserEntity usuario;
    private String descripcion;
    private BigDecimal cantidad;
    private LocalDate fecha;
}