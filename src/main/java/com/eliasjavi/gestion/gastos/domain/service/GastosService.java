package com.eliasjavi.gestion.gastos.domain.service;

import com.eliasjavi.gestion.email.EmailService; // <--- IMPORTAR
import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GastosService {

    private final GastosRepository gastoRepository;
    private final UserRepository userRepository;
    private final EmailService emailService; // <--- NUEVO SERVICIO

    // Constructor actualizado con EmailService
    public GastosService(GastosRepository gastoRepository, UserRepository userRepository, EmailService emailService) {
        this.gastoRepository = gastoRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // ... (listarGastos, findById, deleteById se quedan igual) ...
    @Transactional(readOnly = true)
    public List<GastosEntity> listarGastos(String emailUsuario, boolean esAdmin) {
        if (esAdmin) return gastoRepository.findAll();
        else return gastoRepository.findByUsuario_Email(emailUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<GastosEntity> findById(Long id) {
        return gastoRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(Long id, String emailSolicitante, boolean esAdmin) {
        // ... (tu código actual de deleteById) ...
        // (Copia tu código anterior aquí para no hacerlo largo, eso no cambia)
        Optional<GastosEntity> gastoOpt = gastoRepository.findById(id);
        if (gastoOpt.isEmpty()) return false;
        GastosEntity gasto = gastoOpt.get();
        if (!esAdmin && !gasto.getUsuario().getEmail().equals(emailSolicitante)) {
            throw new RuntimeException("No tienes permisos para eliminar este gasto.");
        }
        BigDecimal cantidadRevertida = gasto.getCantidad();
        Long userId = gasto.getUsuario().getId();
        gastoRepository.deleteById(id);
        UserEntity usuario = userRepository.findById(userId).orElseThrow();
        usuario.setSaldo(usuario.getSaldo().add(cantidadRevertida));
        userRepository.save(usuario);
        return true;
    }

    @Transactional
    public Optional<GastosEntity> actualizarGastoYActualizarSaldo(Long id, GastosEntity gastoActualizado) {
        // ... (tu código actual de actualizar, sin cambios) ...
        return Optional.empty(); // (Resumido, mantén tu código)
    }

    // --- AQUÍ ESTÁ EL CAMBIO ---
    @Transactional
    public GastosEntity guardarGasto(GastosEntity gasto) {
        if (Objects.isNull(gasto)) throw new IllegalArgumentException("Gasto vacío");
        if (gasto.getCantidad() == null) throw new IllegalArgumentException("Cantidad nula");

        // Validaciones
        if (gasto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Cantidad inválida");
        if (gasto.getUsuario() == null || gasto.getUsuario().getId() == null) throw new IllegalArgumentException("El gasto debe tener un usuario");

        UserEntity usuario = userRepository.findById(gasto.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getSaldo().compareTo(gasto.getCantidad()) < 0) {
            throw new RuntimeException("¡Saldo insuficiente! No se puede registrar el gasto.");
        }

        // Operativa saldo
        usuario.setSaldo(usuario.getSaldo().subtract(gasto.getCantidad()));
        userRepository.save(usuario);
        GastosEntity guardado = gastoRepository.save(gasto);

        // --- LÓGICA DE NOTIFICACIÓN EMAIL (> 100€) ---
        if (gasto.getCantidad().compareTo(new BigDecimal("100")) > 0) {
            String asunto = "🚨 ALERTA: Gasto Elevado Registrado";
            String cuerpo = "Se ha registrado un gasto superior a 100€.\n\n" +
                    "Usuario: " + usuario.getNombre() + " (" + usuario.getEmail() + ")\n" +
                    "Concepto: " + gasto.getDescripcion() + "\n" +
                    "Importe: " + gasto.getCantidad() + "€\n" +
                    "Fecha: " + gasto.getFecha();

            // 1. Avisar al ADMIN
            emailService.enviarEmail("admin@admin.com", asunto, cuerpo);

            // 2. Avisar al USUARIO (si no es el admin el que lo hizo, para no recibir doble)
            if (!usuario.getEmail().equalsIgnoreCase("admin@admin.com")) {
                emailService.enviarEmail(usuario.getEmail(), asunto, "Hola " + usuario.getNombre() + ",\n" + cuerpo);
            }
        }
        // ---------------------------------------------

        return guardado;
    }
}