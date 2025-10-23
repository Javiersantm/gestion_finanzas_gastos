package com.eliasjavi.gestion.gastos.domain.service;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import com.eliasjavi.gestion.gastos.domain.repository.GastosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GastosService {
    @Autowired
    private GastosRepository gastoRepository;

    public List<GastosEntity> listarGastos() {
        return gastoRepository.findAll();
    }

    public void guardarGasto(GastosEntity gasto) {
        gastoRepository.save(gasto);
    }
}