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
        if (gasto.getCantidad() <= 0) throw new IllegalArgumentException("Cantidad inválida");
        if (gasto.getUsuario() == null) throw new IllegalArgumentException("El gasto debe tener un usuario");

        // Buscamos al usuario real en la BBDD
        UserEntity usuario = userRepository.findById(gasto.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ¡Validamos que tenga saldo suficiente!
        if (usuario.getSaldo() < gasto.getCantidad()) {
            throw new RuntimeException("¡Saldo insuficiente! No se puede registrar el gasto.");
        }
        // ¡Actualizamos el saldo! (Restamos)
        usuario.setSaldo( usuario.getSaldo() - gasto.getCantidad() );
        // Guardamos al usuario con su nuevo saldo
        userRepository.save(usuario);
        // Guardamos el gasto (como antes)
        return gastoRepository.save(gasto);
    }
    @Transactional
    public boolean deleteById(Long id) {
        if (!gastoRepository.existsById(id)) {
            return false;
        }
        gastoRepository.deleteById(id);
        return true;
    }
}