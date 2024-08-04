//package com.foodiestrip.util;
//
//import com.foodiestrip.config.jwt.JwtProperties;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JWTUtil {
//
//	private final JwtProperties jwtProperties;
//	private final Key key;
//
//	public JWTUtil(JwtProperties jwtProperties) {
//		this.jwtProperties = jwtProperties;
//		this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
//	}
//
//	public String createAccessToken(String userId) {
//		return createToken(userId, "access-token", jwtProperties.getAccessTokenExpireTime());
//	}
//
//	public String createRefreshToken(String userId) {
//		return createToken(userId, "refresh-token", jwtProperties.getRefreshTokenExpireTime());
//	}
//
//	private String createToken(String userId, String subject, long expireTime) {
//		Claims claims = Jwts.claims()
//				.setSubject(subject)
//				.setIssuedAt(new Date())
//				.setExpiration(new Date(System.currentTimeMillis() + expireTime));
//
//		claims.put("userId", userId);
//
//		return Jwts.builder()
//				.setHeaderParam("typ", "JWT")
//				.setClaims(claims)
//				.signWith(key, SignatureAlgorithm.HS256)
//				.compact();
//	}
//
//	public boolean validateToken(String token) {
//		try {
//			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	public String getUserIdFromToken(String token) {
//		Claims claims = Jwts.parserBuilder()
//				.setSigningKey(key)
//				.build()
//				.parseClaimsJws(token)
//				.getBody();
//
//		return claims.get("userId", String.class);
//	}
//}