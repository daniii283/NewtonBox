package com.newtonbox.controllers;

import com.newtonbox.services.Impl.UserService;
import com.newtonbox.dto.ChangePasswordDTO;
import com.newtonbox.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@PreAuthorize("isAuthenticated()")

public class UserController {

    @Autowired
    private UserService userService;

    // ------------------ Métodos CRUD ------------------

    // Obtener todos los usuarios (Solo ADMIN)
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // Obtener un usuario por ID
    // -> Solo ADMIN o si es el usuario auntenticado
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO userDTO = userService.findById(id);
        return ResponseEntity.ok(userDTO);
    }

    /* La petición POST sera manejado en otro controlador */

    @PutMapping("/{id}/changePassword")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok("Password updated succesfully");
    }

    // Borrar un usuario (Solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ Métodos Personalizados ------------------

    // Buscar un usuario por su nombre
    // -> Solo ADmin o si es el mismo usuario autenticado
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #username == authentication.principal.username ")
    public ResponseEntity<UserDTO> findByUsername(@PathVariable String username) {
        UserDTO user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Verificar si existe un usuario con cierto nombre (Solo ADMIN)
    @GetMapping("/exist/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> existByUsername(@PathVariable String username) {
        boolean exist = userService.existByUsername(username);
        return ResponseEntity.ok(exist);
    }
}
