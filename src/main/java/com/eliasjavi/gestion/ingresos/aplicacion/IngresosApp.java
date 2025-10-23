package com.eliasjavi.gestion.ingresos.aplicacion;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngresosApp {

    private final IngresosRepository repository;

    public IngresosEntity registrarIngreso(IngresosEntity ingreso) {
        // Validaciones, lógica de negocio, etc.
        return repository.save(ingreso);
    }

    public List<IngresosEntity> listarIngresos() {
        return repository.findAll();
    }
}