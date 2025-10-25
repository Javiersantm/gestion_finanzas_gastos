package com.eliasjavi.gestion.gastos.aplicacion;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class GastosApp {

    private final GastosRepository repository;

    public GastosEntity registrarGasto(GastosEntity gasto) {
        if (gasto == null) throw new IllegalArgumentException("Gasto vacío");
        if (gasto.getCantidad() == null || gasto.getCantidad().doubleValue() <= 0)
            throw new IllegalArgumentException("Importe inválido");
        return repository.save(gasto);
    }

    public List<GastosEntity> listarGastos() {
        return repository.findAll();
    }
}
