package com.newtonbox.services.Impl;

import com.newtonbox.models.Role;
import com.newtonbox.models.UserEntity;
import com.newtonbox.repository.IRoleRepository;
import com.newtonbox.repository.IUserRepository;
import com.newtonbox.services.IUserService;
import com.newtonbox.dto.ChangePasswordDTO;
import com.newtonbox.dto.UserDTO;
import com.newtonbox.mapper.UserMapper;
import com.newtonbox.utils.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;


@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IRoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> findAll() {
        return userRepo.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Long id) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID:" + id));

        return UserMapper.toDTO(user);
    }

    // --- Personalizado--
    @Override
    public UserDTO findByUsername(String username) {
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User " + username + " not found"));

        return UserMapper.toDTO(user);

    }

    // --- Personalizado--
    @Override
    public boolean existByUsername(String username) {
        return userRepo.existsByUsername(username);
    }


    @Override
    public UserDTO save(UserDTO userDTO) {
        UserEntity user = UserMapper.toEntity(userDTO);

        if (user.getPassword() != null && !user.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }else {
            throw new RuntimeException("Password is required for registration");
        }

        // Asignar rol por defecto: RESEARCHER
        Role defaultRole = roleRepo.findByRoleEnum(RoleEnum.RESEARCHER)
                .orElseThrow(() -> new RuntimeException("Default role RESEARCHER not found"));
        user.getRoles().add(defaultRole);

        // Guardar en bases de datos
        UserEntity userSaved = userRepo.save(user);

        // Devolver el DTO
        return UserMapper.toDTO(userSaved);
    }

    @Override
    public UserDTO update(Long id, UserDTO userDTO) {
        // 1. Buscar en la base de datos
        UserEntity existingUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // 2. Si el nombre no es nulo ni vacio, actualizarlo
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        // 3. Si los roles no son nulos ni vacíos, asignarlos correctamente
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<Role> updateRoles = userDTO.getRoles().stream()
                    .map(roleName -> { // roleName es un String con el nombre del rol
                        // Buscamos el rol en la base de datos usando RoleEnum y lo devolvemos, lanzando excepción si no existe
                        Role role = roleRepo.findByRoleEnum(RoleEnum.valueOf(roleName))
                                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName)); // Lanzamos excepción si no se encuentra el rol
                        return role; // Devolvemos el objeto Role
                    })


                    .collect(Collectors.toSet()); // Recogemos los roles en un Set
            existingUser.setRoles(updateRoles); // Asignamos los roles al usuario existente
        }


        // 4. Guardar los cambios en la base de datos
        UserEntity savedUser = userRepo.save(existingUser);

        // 5. Convertir la entidad a DTO y devolverla
        return UserMapper.toDTO(savedUser);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        // 1. Buscar el usuario en la base de datos
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Verificar la contraseña actual con la que esta guardada en BD
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
            throw new RuntimeException("The current password is not correct");
        }

        // 3. Comprobar que newPassword y confirmNewPassword coincidan
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())){
            throw new RuntimeException("The new passwords do not match");
        }
        // 4. Encriptar la nueva contraseña y guardarla
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepo.save(user);
    }


    @Override
    public void delete(Long id) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        userRepo.delete(user);
    }
}
