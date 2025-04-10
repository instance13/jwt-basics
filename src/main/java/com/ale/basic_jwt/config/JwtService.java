package com.ale.basic_jwt.config;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private static final Dotenv dotenv = Dotenv.load();

  private static final String SECRET_KEY = dotenv.get("JWT_SECRET_KEY");

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // example of functional programming in java
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // without private claims
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  // Map<String, Object> extraClaims is the collection of private claims
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts
        .builder() // builder pattern, but not lombok
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build().parseClaimsJws(token) // this returns Jws<Claims>
        .getBody();
  }

  // i think this method is causing trouble
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    // System.out.printf("---------\nSecret key value: %s\n----------", keyBytes);
    System.out.println("Decoded key length: " + keyBytes.length); // should be at least 32
    System.out.println("Decoded key (Base64): " + Base64.getEncoder().encodeToString(keyBytes));
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
