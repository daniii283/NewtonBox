package com.newtonbox.controllers;

import com.newtonbox.exceptions.unauthorized.InvalidCredentialsException;
import com.newtonbox.exceptions.conflict.UserAlreadyExistsException;
import com.newtonbox.security.Jwt.JwtUtils;
import com.newtonbox.dto.Auth.LoginDTO;
import com.newtonbox.dto.Auth.RegisterDTO;
import com.newtonbox.dto.UserDTO;
import com.newtonbox.mapper.Auth.RegisterMapper;
import com.newtonbox.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    // Registro de usuario
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDTO> save(@RequestBody RegisterDTO registerDTO) {
        UserDTO userDTO = RegisterMapper.toUserDTO(registerDTO);

        // Verificar si el usuario ya existe
        if (userService.existByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException(userDTO.getUsername());

        }

        UserDTO savedUser = userService.save(userDTO);
        return ResponseEntity.ok(savedUser);
    }

    // Login de usuario
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(loginDTO.getUsername());

            return ResponseEntity.ok().body(jwt);

        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }

    }
}
