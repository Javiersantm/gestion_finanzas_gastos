package com.eliasjavi.gestion.usuarios.domain.repository;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    // --- NUEVA CONSULTA ---
    // COALESCE sirve para que si no hay usuarios, devuelva 0 en vez de null
    @Query("SELECT COALESCE(SUM(u.saldo), 0) FROM UserEntity u")
    BigDecimal sumarSaldosDeTodos();
}