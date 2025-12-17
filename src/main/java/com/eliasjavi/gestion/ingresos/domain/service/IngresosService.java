package com.eliasjavi.gestion.ingresos.domain.service;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class IngresosService {

    private final IngresosRepository ingresoRepository;
    private final UserRepository userRepository;

    public IngresosService(IngresosRepository ingresoRepository, UserRepository userRepository) {
        this.ingresoRepository = ingresoRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lista ingresos dependiendo del rol.
     * Admin: Ve todo.
     * User: Ve solo lo suyo.
     */
    @Transactional(readOnly = true)
    public List<IngresosEntity> listarIngresos(String emailUsuario, boolean esAdmin) {
        if (esAdmin) {
            return ingresoRepository.findAll();
        } else {
            return ingresoRepository.findByUsuario_Email(emailUsuario);
        }
    }

    @Transactional(readOnly = true)
    public Optional<IngresosEntity> findById(Long id) {
        return ingresoRepository.findById(id);
    }

    @Transactional
    public IngresosEntity guardarIngreso(IngresosEntity ingreso) {
        if (Objects.isNull(ingreso)) throw new IllegalArgumentException("Ingreso vacío");
        if (ingreso.getCantidad() == null) throw new IllegalArgumentException("Cantidad nula");
        if (ingreso.getCantidad().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Cantidad inválida");
        if (ingreso.getUsuario() == null || ingreso.getUsuario().getId() == null) {
            throw new IllegalArgumentException("El ingreso debe estar ligado a un usuario con ID válido");
        }

        BigDecimal cantidadNueva = ingreso.getCantidad();
        BigDecimal cantidadOriginal = BigDecimal.ZERO;

        // 1. Manejo de Edición
        if (ingreso.getId() != null) {
            IngresosEntity ingresoOriginal = ingresoRepository.findById(ingreso.getId())
                    .orElseThrow(() -> new RuntimeException("Ingreso a editar no encontrado."));
            cantidadOriginal = ingresoOriginal.getCantidad();
        }

        // 2. Buscamos y preparamos al usuario
        UserEntity usuario = userRepository.findById(ingreso.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getSaldo() == null) {
            usuario.setSaldo(BigDecimal.ZERO);
        }

        // 3. Ajuste del saldo
        BigDecimal saldoTemporal = usuario.getSaldo().subtract(cantidadOriginal);
        usuario.setSaldo(saldoTemporal.add(cantidadNueva));

        // 4. Guardar
        userRepository.save(usuario);
        return ingresoRepository.save(ingreso);
    }

    /**
     * Elimina con seguridad: Verifica si el usuario tiene permiso (es dueño o Admin).
     */
    @Transactional
    public boolean deleteById(Long id, String emailSolicitante, boolean esAdmin) {
        Optional<IngresosEntity> ingresoOpt = ingresoRepository.findById(id);

        if (ingresoOpt.isEmpty()) {
            return false;
        }

        IngresosEntity ingreso = ingresoOpt.get();

        // --- SEGURIDAD ---
        // Si NO es admin Y el ingreso NO es suyo -> Error
        if (!esAdmin && !ingreso.getUsuario().getEmail().equals(emailSolicitante)) {
            throw new RuntimeException("No tienes permisos para eliminar este ingreso.");
        }

        BigDecimal cantidadAEliminar = ingreso.getCantidad();
        Long userId = ingreso.getUsuario().getId();

        // 1. Eliminar
        ingresoRepository.delete(ingreso);

        // 2. Ajuste de saldo
        UserEntity usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario asociado no encontrado."));

        usuario.setSaldo(usuario.getSaldo().subtract(cantidadAEliminar));
        userRepository.save(usuario);

        return true;
    }
}