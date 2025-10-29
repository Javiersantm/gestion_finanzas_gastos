package com.eliasjavi.gestion.gastos.domain.repository;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ¡Añade esta importación!
import org.springframework.stereotype.Repository;

@Repository
public interface GastosRepository extends JpaRepository<GastosEntity, Long> {

    @Query("SELECT SUM(g.cantidad) FROM GastosEntity g")
    Double sumarTodosLosGastos();
}