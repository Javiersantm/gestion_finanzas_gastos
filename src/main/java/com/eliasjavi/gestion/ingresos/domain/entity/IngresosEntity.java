package com.eliasjavi.gestion.ingresos.domain.entity;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

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

    @NotNull // Un ingreso siempre debe estar ligado a un usuario
    @ManyToOne(fetch = FetchType.LAZY) // Relación: Muchos ingresos pertenecen a Un usuario
    @JoinColumn(name = "usuario_id") // Así se llama la columna en la BBDD
    private UserEntity usuario;
    private String fuente;
    private Double cantidad;
    private LocalDate fecha;
}