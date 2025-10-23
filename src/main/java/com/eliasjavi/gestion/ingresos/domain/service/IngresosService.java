package com.eliasjavi.gestion.ingresos.domain.service;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import com.eliasjavi.gestion.ingresos.domain.repository.IngresosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngresosService {
    @Autowired
    private IngresosRepository ingresoRepository;

    public List<IngresosEntity> listarIngresos() {
        return ingresoRepository.findAll();
    }

    public void guardarIngreso(IngresosEntity ingreso) {
        ingresoRepository.save(ingreso);
    }
}
