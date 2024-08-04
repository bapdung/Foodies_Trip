package com.foodiestrip.config.jwt;

import com.foodiestrip.model.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Log4j2
public class TokenProvider {
    private final JwtProperties jwtProperties;
    private final Key key;

    @Autowired
    public TokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(UserDto user, Duration expiredAt) {
        Date now = new Date();
        String token = makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
        log.info("Generated token: {}", token);
        return token;
    }

    @Operation(summary = "JWT Token 생성")
    private String makeToken(Date expiry, UserDto user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(io.jsonwebtoken.Header.TYPE, io.jsonwebtoken.Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmailId() + user.getEmailDomain())
                .claim("id", user.getUserId())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Operation(summary = "JWT Token 유효성 검사")
    public boolean validToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT Token Error 발생: {}", e.getMessage());
            return false;
        }
    }

    @Operation(summary = "Token 기반으로 인증 정보 호출")
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    @Operation(summary = "Token 기반으로 UserId 호출")
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }
}