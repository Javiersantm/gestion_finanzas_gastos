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

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email incorrecto")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El saldo no puede ser nulo")
    private BigDecimal saldo;

    public UserEntity() {
        this.saldo = BigDecimal.ZERO;
    }
}