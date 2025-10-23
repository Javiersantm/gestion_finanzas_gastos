package com.eliasjavi.gestion.ingresos.domain.repository;

import com.eliasjavi.gestion.ingresos.domain.entity.IngresosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngresosRepository extends JpaRepository<IngresosEntity, Long> {}