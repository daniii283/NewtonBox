package com.newtonbox.repository;

import com.newtonbox.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface
IUserRepository extends JpaRepository<UserEntity, Long> {

    // Busca usuario por su nombre
    Optional<UserEntity> findByUsername(String username);

    // Verifica si existe un usuario con ese nombre
    boolean existsByUsername(String username);
}
