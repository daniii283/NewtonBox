package com.newtonbox.mapper.Auth;

import com.newtonbox.models.UserEntity;
import com.newtonbox.dto.Auth.LoginDTO;

public class LoginMapper {

    public static UserEntity toEntity(LoginDTO loginDTO) {
        if (loginDTO == null) return null;

        return UserEntity.builder()
                .username(loginDTO.getUsername())
                .password(loginDTO.getPassword()) // ✅ Necesario para el login
                .build();
    }

    // No incluimos toDTO porque LoginDTO no se devuelve nunca al frontend
}
