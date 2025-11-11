package com.viandasApp.api.Usuario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    // CONSTRUCTOR
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        // Decodifica la clave en bytes y genera un Key para HMAC-SHA256
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    // GENERAR TOKEN
    public String generateToken(String subject,String... roles){
        Map<String, Object> claims = Map.of("roles",Arrays.asList(roles));
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)    // subject: quien es el owner (email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();     // String JWT
    }
    /// Genero el token con subject (el user: email) y sus claims (rles)
    /// String... roles -> varargs (variable arguments)
    ///Permite pasar 0, 1 o muchos argumentos del tipo String a un metodo. y lo convierte en un arreglo

    // PARSEO EL TOKEN Y VALIDO LA FIRMA
    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // EXTRAE CLAIM GENERICO
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    // DEVOLVER EL SUBJECT (email)
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // DEVOLVER FECHA DE EXPIRACION
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // INDICAR SI EL TOKEN EXPIRO
    public boolean isTokenExpired(String token){
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // VALIDO QUE EL SUBJECT COINCIDA Y NO ESTE EXPIRADO
    public boolean validateToken(String token, String username){
        final String subject = extractUsername(token);
        return subject.equals(username) && !isTokenExpired(token);
    }

    // EXTRAIGO LOS ROLES
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token){
        Claims claims = parseClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }
        return Collections.emptyList();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
