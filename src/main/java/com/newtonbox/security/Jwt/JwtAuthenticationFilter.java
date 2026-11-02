package com.newtonbox.security.Jwt;

import com.newtonbox.services.Impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token, simplemente pasa la solicitud sin modificar nada.
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);  // Extraemos el token sin modificarlo.
        System.out.println("JWT received: " + jwt);

        // Validamos el token
        if (!jwtUtils.validateJwtToken(jwt)) {
            // Si el token no es válido, simplemente pasa la solicitud sin modificar nada.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT no válido.");
            return;  // No continuamos con el filtro si el token es inválido.
        }

        // Extraemos el nombre de usuario del token.
        username = jwtUtils.getUsernameFromJwtToken(jwt);
        System.out.println("JWT token belongs to user: " + username);

        // Si el token es válido, configuramos el contexto de seguridad.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Creamos el token de autenticación
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Depuración adicional
            System.out.println("Autenticado como: " + username);
            System.out.println("Authorities asignadas: " + userDetails.getAuthorities());
        }

        // Llamamos al siguiente filtro en la cadena sin alterar el valor del token.
        filterChain.doFilter(request, response);
    }
}

