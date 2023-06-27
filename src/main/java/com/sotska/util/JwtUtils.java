package com.sotska.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtils {
    private final static String JWT_SIGN_KEY = "secret";

    public String createToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails);
    }

    public String createToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .signWith(SignatureAlgorithm.HS512, JWT_SIGN_KEY).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        var userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Optional<String> extractToken(String token) {
        return token != null && token.startsWith("Bearer") ?
                Optional.of(token.substring(6)) :
                Optional.empty();

    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(JWT_SIGN_KEY).parseClaimsJws(token).getBody();
    }
}
