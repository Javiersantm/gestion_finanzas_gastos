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
        if (ingreso.getCantidad() <= 0) throw new IllegalArgumentException("Cantidad inválida");
        if (ingreso.getUsuario() == null) throw new IllegalArgumentException("El ingreso debe tener un usuario");

        // Buscamos al usuario real en la BBDD usando el ID que viene en el ingreso
        UserEntity usuario = userRepository.findById(ingreso.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ¡Actualizamos el saldo!
        usuario.setSaldo( usuario.getSaldo() + ingreso.getCantidad() );

        // Guardamos al usuario con su nuevo saldo
        userRepository.save(usuario);

        // Guardamos el ingreso (como antes)
        return ingresoRepository.save(ingreso);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!ingresoRepository.existsById(id)) {
            return false;
        }
        ingresoRepository.deleteById(id);
        return true;
    }
}