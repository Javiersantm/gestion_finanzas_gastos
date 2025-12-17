package com.eliasjavi.gestion.gastos.domain.repository;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastosRepository extends JpaRepository<GastosEntity, Long> {

    // 1. Ver SOLO mis gastos
    List<GastosEntity> findByUsuario_Email(String email);

    // 2. Sumar gastos de UN usuario
    @Query("SELECT COALESCE(SUM(g.cantidad), 0) FROM GastosEntity g WHERE g.usuario.email = :email AND g.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarGastosPorUsuarioYFecha(@Param("email") String email, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // 3. Sumar gastos GLOBALES (Toda la casa - Nuevo para el Dashboard)
    @Query("SELECT COALESCE(SUM(g.cantidad), 0) FROM GastosEntity g WHERE g.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarGastosGlobales(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT g.categoria, SUM(g.cantidad) FROM GastosEntity g " +
            "WHERE g.fecha BETWEEN :inicio AND :fin " + // Si quieres filtrar por usuario añade: AND g.usuario.email = :email
            "GROUP BY g.categoria")
    List<Object[]> sumarGastosPorCategoria(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}