package com.eliasjavi.gestion.usuarios.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "usuarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String nombre;

    @NotBlank
    private String password;

    @NotNull
    private BigDecimal saldo;

    // --- NUEVO CAMPO: ROL ---
    // Valores posibles: "USER", "ADMIN"
    private String rol;

    public UserEntity() {
        this.saldo = BigDecimal.ZERO;
        this.rol = "USER"; // Por defecto todos son usuarios normales
    }
}