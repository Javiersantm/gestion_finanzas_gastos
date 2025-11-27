package com.eliasjavi.gestion.gastos.domain.service;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class GastosService {

    private final GastosRepository gastoRepository;
    private final UserRepository userRepository;

    public GastosService(GastosRepository gastoRepository, UserRepository userRepository) {
        this.gastoRepository = gastoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<GastosEntity> listarGastos() {
        return gastoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<GastosEntity> findById(Long id) {
        return gastoRepository.findById(id);
    }

    @Transactional
    public GastosEntity guardarGasto(GastosEntity gasto) {
        if (Objects.isNull(gasto)) throw new IllegalArgumentException("Gasto vacío");
        if (gasto.getCantidad() == null) throw new IllegalArgumentException("Cantidad nula");

        // Verificación de Cantidad > 0
        if (gasto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Cantidad inválida");

        if (gasto.getUsuario() == null || gasto.getUsuario().getId() == null) throw new IllegalArgumentException("El gasto debe tener un usuario");

        // Buscamos al usuario real en la BBDD
        UserEntity usuario = userRepository.findById(gasto.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validación de Saldo Suficiente
        if (usuario.getSaldo().compareTo(gasto.getCantidad()) < 0) {
            throw new RuntimeException("¡Saldo insuficiente! No se puede registrar el gasto.");
        }

        // Resta al saldo
        usuario.setSaldo( usuario.getSaldo().subtract(gasto.getCantidad()) );

        // Guardamos al usuario con su nuevo saldo y el gasto
        userRepository.save(usuario);
        return gastoRepository.save(gasto);
    }

    /**
     * Elimina un gasto y revierte el saldo del usuario SUMANDO la cantidad.
     */
    @Transactional
    public boolean deleteById(Long id) {
        Optional<GastosEntity> gastoOpt = gastoRepository.findById(id);

        if (gastoOpt.isEmpty()) {
            return false;
        }

        GastosEntity gasto = gastoOpt.get();
        BigDecimal cantidadRevertida = gasto.getCantidad();
        Long userId = gasto.getUsuario().getId();

        // 1. Eliminamos el gasto
        gastoRepository.deleteById(id);

        // 2. Buscamos al usuario para revertir el saldo
        UserEntity usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado al intentar revertir saldo"));

        // 3. Revertimos la operación: SUMAMOS la cantidad del gasto eliminado
        usuario.setSaldo(usuario.getSaldo().add(cantidadRevertida));
        userRepository.save(usuario);

        return true;
    }

    /**
     * Actualiza un gasto existente, primero revierte el impacto del gasto antiguo en el saldo
     * y luego aplica el impacto del nuevo gasto.
     */
    @Transactional
    public Optional<GastosEntity> actualizarGastoYActualizarSaldo(Long id, GastosEntity gastoActualizado) {
        Optional<GastosEntity> gastoExistenteOpt = gastoRepository.findById(id);

        if (gastoExistenteOpt.isEmpty()) {
            return Optional.empty();
        }

        GastosEntity gastoExistente = gastoExistenteOpt.get();

        // Asumiendo que el usuario no cambia, usamos el existente
        Long userId = gastoExistente.getUsuario().getId();
        UserEntity usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualización de gasto"));

        BigDecimal cantidadAntigua = gastoExistente.getCantidad();
        BigDecimal cantidadNueva = gastoActualizado.getCantidad();

        if (cantidadNueva.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cantidad de gasto actualizada inválida");
        }

        // 1. Revertir el gasto antiguo (Sumar la cantidad antigua al saldo)
        BigDecimal saldoTemporal = usuario.getSaldo().add(cantidadAntigua);

        // 2. Verificar si hay saldo suficiente para el nuevo gasto
        if (saldoTemporal.compareTo(cantidadNueva) < 0) {
            throw new RuntimeException("¡Saldo insuficiente! La actualización excede el saldo disponible.");
        }

        // 3. Aplicar el nuevo gasto (Restar la cantidad nueva)
        usuario.setSaldo(saldoTemporal.subtract(cantidadNueva));

        // 4. Actualizar la entidad Gasto en la BBDD
        gastoExistente.setDescripcion(gastoActualizado.getDescripcion());
        gastoExistente.setCantidad(cantidadNueva);
        gastoExistente.setFecha(gastoActualizado.getFecha());
        gastoExistente.setUsuario(usuario);

        // 5. Guardar el usuario con el nuevo saldo y el gasto actualizado
        userRepository.save(usuario);
        GastosEntity actualizado = gastoRepository.save(gastoExistente);

        return Optional.of(actualizado);
    }
}
