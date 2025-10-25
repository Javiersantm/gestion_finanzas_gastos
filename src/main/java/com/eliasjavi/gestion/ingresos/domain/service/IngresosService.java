package com.eliasjavi.gestion.ingresos.domain.service;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class IngresosService {

    private final IngresosRepository ingresoRepository;

    public IngresosService(IngresosRepository ingresoRepository) {
        this.ingresoRepository = ingresoRepository;
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
