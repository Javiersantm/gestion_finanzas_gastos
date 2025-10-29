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
import java.math.BigDecimal; // Importación necesaria

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

    @Transactional
    public IngresosEntity guardarIngreso(IngresosEntity ingreso) {
        if (Objects.isNull(ingreso)) throw new IllegalArgumentException("Ingreso vacío");
        if (ingreso.getCantidad() == null) throw new IllegalArgumentException("Cantidad nula");

        // 1. Verificación de Cantidad (Tu lógica con BigDecimal)
        if (ingreso.getCantidad().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Cantidad inválida");

        // 2. Verificación de Usuario (Lógica del compañero)
        if (ingreso.getUsuario() == null || ingreso.getUsuario().getId() == null) {
            throw new IllegalArgumentException("El ingreso debe estar ligado a un usuario con ID válido");
        }

        // 3. Buscamos al usuario real para actualizar el saldo
        UserEntity usuario = userRepository.findById(ingreso.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 4. Actualizamos el saldo
        // Nota: Conversión a doubleValue() porque asumimos que UserEntity.saldo es Double.
        usuario.setSaldo(usuario.getSaldo() + ingreso.getCantidad().doubleValue());

        // 5. Guardamos al usuario con su nuevo saldo
        userRepository.save(usuario);

        // 6. Guardamos el ingreso
        return ingresoRepository.save(ingreso);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!ingresoRepository.existsById(id)) {
            return false;
        }
        // Nota: Si el deleteById debe ajustar el saldo, la lógica debe ir aquí también.
        // Asumiendo que esta es solo la eliminación simple por ahora.
        ingresoRepository.deleteById(id);
        return true;
    }
}