package com.newtonbox.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

/**
 * Clase personalizada que extiende org.springframework.security.userdetails.User
 * para añadir un campo 'id' que representa el ID del usuario en la base de datos
 */
@Getter
public class CustomUserDetails extends User {

    private Long id; // Añadimos el campo de la id

    public CustomUserDetails(Long id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }
}
