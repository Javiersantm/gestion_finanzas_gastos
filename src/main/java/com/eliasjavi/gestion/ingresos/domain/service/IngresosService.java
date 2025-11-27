package com.eliasjavi.gestion.ingresos.domain.service;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class IngresosService {

    private final IngresosRepository ingresoRepository;
    private final UserRepository userRepository;

    public IngresosService(IngresosRepository ingresoRepository, UserRepository userRepository) {
        this.ingresoRepository = ingresoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<IngresosEntity> listarIngresos() {
        return ingresoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<IngresosEntity> findById(Long id) {
        return ingresoRepository.findById(id);
    }

    /**
     * Guarda o actualiza un ingreso y ajusta el saldo del usuario correctamente.
     * Si el ID existe, se trata como una edición.
     */
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

        // 1. Manejo de Edición vs. Creación
        if (ingreso.getId() != null) {
            // Es una EDICIÓN:
            // Obtenemos la cantidad original antes de guardar los cambios
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

        // 3. Ajuste del saldo: Restamos lo viejo y sumamos lo nuevo
        // Primero, revertimos la cantidad original (solo si es edición)
        BigDecimal saldoTemporal = usuario.getSaldo().subtract(cantidadOriginal);

        // Luego, sumamos la nueva cantidad
        usuario.setSaldo(saldoTemporal.add(cantidadNueva));

        // 4. Guardamos el usuario y el ingreso
        userRepository.save(usuario);
        return ingresoRepository.save(ingreso);
    }

    /**
     * Elimina un ingreso por ID y actualiza el saldo del usuario restando la cantidad.
     * @param id El ID del ingreso a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    @Transactional
    public boolean deleteById(Long id) {
        Optional<IngresosEntity> ingresoOptional = ingresoRepository.findById(id);

        if (ingresoOptional.isEmpty()) {
            return false;
        }

        IngresosEntity ingreso = ingresoOptional.get();
        BigDecimal cantidadAEliminar = ingreso.getCantidad();
        Long userId = ingreso.getUsuario().getId();

        // 1. Eliminación de la base de datos
        ingresoRepository.delete(ingreso);

        // 2. Ajuste de saldo
        UserEntity usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario asociado al ingreso no encontrado."));

        if (usuario.getSaldo() == null) {
            usuario.setSaldo(BigDecimal.ZERO);
        }

        usuario.setSaldo(usuario.getSaldo().subtract(cantidadAEliminar));

        userRepository.save(usuario);

        return true;
    }
}
