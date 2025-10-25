package com.eliasjavi.gestion.gastos.domain.service;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GastosService {

    private final GastosRepository gastoRepository;

    public GastosService(GastosRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
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
