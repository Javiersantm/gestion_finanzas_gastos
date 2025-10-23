package com.eliasjavi.gestion.gastos.aplicacion;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GastosApp {

    private final GastosRepository repository;

    public GastosEntity registrarGasto(GastosEntity gasto) {
        // Validaciones, lógica de negocio, etc.
        return repository.save(gasto);
    }

    public List<GastosEntity> listarGastos() {
        return repository.findAll();
    }
}