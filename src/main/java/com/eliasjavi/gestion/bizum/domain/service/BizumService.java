package com.eliasjavi.gestion.bizum.domain.service;
import com.eliasjavi.gestion.bizum.infraestructura.dto.EnviarBizumDTO;
import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.service.IngresosService;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BizumService {

    // Este servicio de "Bizum" orquesta a los otros servicios
    private final IngresosService ingresosService;
    private final UserRepository userRepository;

    @Transactional
    public void enviarBizum(EnviarBizumDTO bizumDTO) {

        // Encontrar al usuario destinatario
        UserEntity destinatario = userRepository.findByEmail(bizumDTO.getEmailDestinatario())
                .orElseThrow(() -> new RuntimeException("Email del destinatario no encontrado: " + bizumDTO.getEmailDestinatario()));

        // Crear la entidad Ingreso
        IngresosEntity nuevoIngreso = new IngresosEntity();
        nuevoIngreso.setUsuario(destinatario);
        nuevoIngreso.setCantidad(bizumDTO.getCantidad());
        nuevoIngreso.setFuente("Bizum recibido: " + bizumDTO.getConcepto());
        nuevoIngreso.setFecha(LocalDate.now());

        // Llamar al servicio de dominio de Ingresos para guardar
        // (Esto se encargará de actualizar el saldo y guardar el ingreso)
        ingresosService.guardarIngreso(nuevoIngreso);
    }
}
