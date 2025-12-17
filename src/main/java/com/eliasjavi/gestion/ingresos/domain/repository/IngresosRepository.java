package com.eliasjavi.gestion.ingresos.domain.repository;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngresosRepository extends JpaRepository<IngresosEntity, Long> {

    // 1. Ver SOLO mis ingresos
    List<IngresosEntity> findByUsuario_Email(String email);

    // 2. Sumar ingresos de UN usuario (para validaciones o lógica futura)
    @Query("SELECT COALESCE(SUM(i.cantidad), 0) FROM IngresosEntity i WHERE i.usuario.email = :email AND i.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosPorUsuarioYFecha(@Param("email") String email, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // 3. Sumar ingresos GLOBALES (Toda la casa - Nuevo para el Dashboard)
    @Query("SELECT COALESCE(SUM(i.cantidad), 0) FROM IngresosEntity i WHERE i.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosGlobales(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}