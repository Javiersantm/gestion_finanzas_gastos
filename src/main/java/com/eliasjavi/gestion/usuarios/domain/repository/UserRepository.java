package com.eliasjavi.gestion.usuarios.domain.repository;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Le dice a Spring que esto es para acceder a la BBDD
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

}