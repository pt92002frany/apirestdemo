package edu.sv.ues.dam235.apirestdemo.utilities;

import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    // Clave secreta de al menos 32 caracteres (256 bits) para HS256
    private final String secretKey = "CPgIMDqBnNEXawGoFX7gxYZ1234567890abc";

    // Tiempo de expiración del token: 30 minutos (en milisegundos)
    private final long JWT_EXPIRATION = 1800000;

    /**
     * Obtiene la clave secreta en formato SecretKey (necesario para JJWT 0.12.x).
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el nombre de usuario (subject) del token.
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token usando una función resolver.
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token.
     * Compatible con JJWT 0.12.x
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT para el usuario autenticado.
     */
    public TokenDTO generateToken(String userName, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        claims.put("authorities", authorities);
        return createToken(claims, userName);
    }

    /**
     * Crea el token con los claims y el usuario especificado.
     */
    private TokenDTO createToken(Map<String, Object> claims, String userName) {
        TokenDTO newToken = new TokenDTO();
        newToken.setExpireIn(String.valueOf(JWT_EXPIRATION));

        String token = Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey())
                .compact();

        newToken.setToken(token);
        return newToken;
    }

    /**
     * Valida que el token pertenezca al usuario y no esté expirado.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Valida el token y establece la autenticación en el contexto de Spring Security.
     */
    public boolean validatedTokenPermission(String token) {
        try {
            String email = extractUsername(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}