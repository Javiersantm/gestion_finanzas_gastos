package com.eliasjavi.gestion.bizum.infraestructura.dto;

import jakarta.validation.constraints.Email;
// La anotación @Min no es compatible con BigDecimal, la eliminamos.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal; // <-- ¡IMPORTACIÓN NECESARIA!

@Data
public class EnviarBizumDTO {

    @NotBlank(message = "Debe indicar el email del destinatario")
    @Email(message = "El email del destinatario no es válido")
    private String emailDestinatario;

    @NotNull(message = "La cantidad no puede ser nula")
    // @Min(value = 1, message = "La cantidad debe ser al menos 1") <-- ELIMINADA
    private BigDecimal cantidad; // <-- CAMBIADO A BIGDECIMAL

    @NotBlank(message = "Debe incluir un concepto o remitente")
    private String concepto;
}