package com.newtonbox.repository;

import com.newtonbox.models.Role;
import com.newtonbox.utils.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleEnum (RoleEnum roleEnum);
}
