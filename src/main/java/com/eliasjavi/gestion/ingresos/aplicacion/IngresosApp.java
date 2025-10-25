package com.eliasjavi.gestion.ingresos.aplicacion;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class IngresosApp {

    private final IngresosRepository repository;

    public IngresosEntity registrarIngreso(IngresosEntity ingreso) {
        if (ingreso == null) throw new IllegalArgumentException("Ingreso vacío");
        if (ingreso.getCantidad() == null || ingreso.getCantidad().doubleValue() <= 0)
            throw new IllegalArgumentException("Cantidad inválida");
        return repository.save(ingreso);
    }

    public List<IngresosEntity> listarIngresos() {
        return repository.findAll();
    }
}
