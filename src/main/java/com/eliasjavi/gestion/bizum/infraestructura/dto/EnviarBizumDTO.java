package com.eliasjavi.gestion.bizum.infraestructura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnviarBizumDTO {

    @NotBlank(message = "Debe indicar el email del destinatario")
    @Email(message = "El email del destinatario no es válido")
    private String emailDestinatario;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Double cantidad;

    @NotBlank(message = "Debe incluir un concepto o remitente")
    private String concepto;
}