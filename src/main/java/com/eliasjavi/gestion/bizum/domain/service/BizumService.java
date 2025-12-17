package com.eliasjavi.gestion.bizum.domain.service;

import com.eliasjavi.gestion.email.EmailService; // <--- IMPORTAR
import com.eliasjavi.gestion.bizum.infraestructura.dto.EnviarBizumDTO;
import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BizumService {

    private final UserRepository userRepository;
    private final IngresosRepository ingresosRepository;
    private final GastosRepository gastosRepository;
    private final EmailService emailService; // <--- INYECTAR SERVICIO

    @Transactional
    public void enviarBizum(String emailRemitente, EnviarBizumDTO bizumDTO) {
        if (emailRemitente.equalsIgnoreCase(bizumDTO.getEmailDestinatario())) {
            throw new RuntimeException("No puedes enviarte un Bizum a ti mismo.");
        }

        UserEntity remitente = userRepository.findByEmail(emailRemitente)
                .orElseThrow(() -> new RuntimeException("Usuario remitente no encontrado."));

        UserEntity destinatario = userRepository.findByEmail(bizumDTO.getEmailDestinatario())
                .orElseThrow(() -> new RuntimeException("El email del destinatario no existe en el sistema."));

        BigDecimal cantidad = bizumDTO.getCantidad();

        if (remitente.getSaldo().compareTo(cantidad) < 0) {
            throw new RuntimeException("Saldo insuficiente para realizar el Bizum.");
        }

        // --- OPERACIÓN 1: GASTO PARA EL REMITENTE ---
        remitente.setSaldo(remitente.getSaldo().subtract(cantidad));
        userRepository.save(remitente);

        GastosEntity gastoSalida = new GastosEntity();
        gastoSalida.setUsuario(remitente);
        gastoSalida.setCantidad(cantidad);
        gastoSalida.setFecha(LocalDate.now());
        gastoSalida.setDescripcion("Bizum enviado a " + destinatario.getNombre() + ": " + bizumDTO.getConcepto());
        gastoSalida.setCategoria(bizumDTO.getCategoria() != null ? bizumDTO.getCategoria() : "Bizum");
        gastosRepository.save(gastoSalida);

        // --- OPERACIÓN 2: INGRESO PARA EL DESTINATARIO ---
        destinatario.setSaldo(destinatario.getSaldo().add(cantidad));
        userRepository.save(destinatario);

        IngresosEntity ingresoEntrada = new IngresosEntity();
        ingresoEntrada.setUsuario(destinatario);
        ingresoEntrada.setCantidad(cantidad);
        ingresoEntrada.setFecha(LocalDate.now());
        ingresoEntrada.setFuente("Bizum recibido de " + remitente.getNombre() + ": " + bizumDTO.getConcepto());
        ingresoEntrada.setCategoria("Bizum");
        ingresosRepository.save(ingresoEntrada);

        // --- NOTIFICACIÓN POR EMAIL AL DESTINATARIO ---
        String asunto = "💰 Has recibido un Bizum de " + remitente.getNombre();
        String cuerpo = "¡Hola " + destinatario.getNombre() + "!\n\n" +
                "Has recibido dinero a través de la App de Gestión.\n" +
                "Remitente: " + remitente.getNombre() + " (" + remitente.getEmail() + ")\n" +
                "Cantidad: " + cantidad + "€\n" +
                "Concepto: " + bizumDTO.getConcepto() + "\n\n" +
                "Tu nuevo saldo es: " + destinatario.getSaldo() + "€";

        emailService.enviarEmail(destinatario.getEmail(), asunto, cuerpo);
        // ----------------------------------------------
    }
}