package com.eliasjavi.gestion.gastos.domain.repository;

import com.eliasjavi.gestion.gastos.domain.entity.GastosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GastosRepository extends JpaRepository<GastosEntity, Long> {}