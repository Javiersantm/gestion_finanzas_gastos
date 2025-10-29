package com.eliasjavi.gestion.gastos.domain.entity;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

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

    @NotNull // Un gasto siempre debe estar ligado a un usuario
    @ManyToOne(fetch = FetchType.LAZY) // Relación: Muchos gastos pertenecen a Un usuario

    @JoinColumn(name = "usuario_id") // Así se llama la columna en la BBDD
    private UserEntity usuario;
    private String descripcion;
    private Double cantidad;
    private LocalDate fecha;
}