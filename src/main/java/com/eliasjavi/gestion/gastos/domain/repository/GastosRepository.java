package com.eliasjavi.gestion.gastos.domain.repository;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal; // Importación de BigDecimal

@Repository
public interface GastosRepository extends JpaRepository<GastosEntity, Long> {

    @Query("SELECT SUM(g.cantidad) FROM GastosEntity g")
    BigDecimal sumarTodosLosGastos(); // Cambiado a BigDecimal
}
