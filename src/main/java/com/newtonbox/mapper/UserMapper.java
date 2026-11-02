package com.newtonbox.mapper;

import com.newtonbox.models.UserEntity;
import com.newtonbox.dto.UserDTO;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(UserEntity user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles() != null ?
                        user.getRoles().stream()  // Obtenemos los roles
                                .map(role -> role.getRoleEnum().name())  // Aquí extraemos solo el nombre del rol
                                .collect(Collectors.toSet())  // Y lo convertimos a un Set<String>
                        : null)
                .build();
    }

    public static UserEntity toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        UserEntity user = new UserEntity();
        user.setId(userDTO.getId());

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getPassword() != null){
            user.setPassword(userDTO.getPassword());
        }

        return user;
    }

}
