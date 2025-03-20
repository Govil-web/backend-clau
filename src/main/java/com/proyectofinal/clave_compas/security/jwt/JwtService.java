package com.proyectofinal.clave_compas.security.jwt;

import com.proyectofinal.clave_compas.security.userdetail.CusUserDetailsService;
import com.proyectofinal.clave_compas.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final CusUserDetailsService cusUserDetailsService;
    private final JwtConstants jwtConstants;
    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConstants.getAccessTokenExpiration()))
                .signWith(getKey() ,SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConstants.getRefreshTokenExpiration())) // 7 días
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateNewAccessToken(String refreshToken) {
        if (isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh Token expirado");
        }
        String username = getUsernameFromToken(refreshToken);

        // Cargar detalles del usuario desde el servicio de detalles de usuario
        UserDetails user = cusUserDetailsService.loadUserByUsername(username);

        // Generar un nuevo Access Token
        return getToken(new HashMap<>(), user);
    }

    private Key getKey() {
        byte[] keyBytes= Decoders.BASE64.decode(jwtConstants.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    private Claims getAllClaims(String token)
    {
        return Jwts
                .parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims=getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token)
    {
        return getExpiration(token).before(new Date());
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean isRefreshTokenValid(String refreshToken, String username) {
        return getUsernameFromToken(refreshToken).equals(username) && !isTokenExpired(refreshToken);
    }
}
