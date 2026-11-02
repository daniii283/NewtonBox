package com.newtonbox.mapper.Auth;

import com.newtonbox.models.UserEntity;
import com.newtonbox.dto.Auth.RegisterDTO;
import com.newtonbox.dto.UserDTO;

public class RegisterMapper {

    public static UserEntity toEntity(RegisterDTO registerDTO) {
        if (registerDTO == null) return null;

        return UserEntity.builder()
                .username(registerDTO.getUsername())
                .password(registerDTO.getPassword()) // ✅ Necesario para el registro
                .build();
    }

    public static UserDTO toUserDTO(RegisterDTO registerDTO) {
        if (registerDTO == null) return null;

        return UserDTO.builder()
                .username(registerDTO.getUsername())
                .password(registerDTO.getPassword()) // aquí se envía solo internamente
                .build();
    }
}
