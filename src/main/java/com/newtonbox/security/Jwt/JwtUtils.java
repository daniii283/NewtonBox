package com.newtonbox.security.Jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            // Verificación del JWT
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT token: " + authToken);
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token: " + authToken);
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + authToken);
        } catch (IllegalArgumentException e) {
            System.out.println("JWT token is null or empty.");
        }
        return false; // Retorna false si hay algún error, pero no cambia el token.
    }
}
