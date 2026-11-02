package com.newtonbox.mapper;

import com.newtonbox.models.Permission;
import com.newtonbox.dto.PermissionDTO;
import com.newtonbox.utils.PermissionEnum;

public class PermissionMapper {

    public static PermissionDTO toDTO(Permission permission) {
        if (permission == null) return null;

        return PermissionDTO.builder()
                .id(permission.getId())
                .permissionEnum(permission.getPermissionEnum().name())
                .build();
    }

    public static Permission toEntity(PermissionDTO permissionDTO) {
        if (permissionDTO == null) return null;
        Permission permission = new Permission();
        permission.setId(permissionDTO.getId());
        permission.setPermissionEnum(PermissionEnum.valueOf(permissionDTO.getPermissionEnum()));
        return permission;
    }
}
