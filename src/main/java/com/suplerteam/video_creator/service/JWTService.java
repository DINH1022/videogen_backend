package com.suplerteam.video_creator.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Key;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${myapp.jwt.secret}")
    private String SECRET_KEY;

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpirationTime(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private  <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSigningKey(){
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(
            Map<String,String> extraClaims,
            UserDetails userDetails){
        if(extraClaims==null){
            extraClaims=new HashMap<>();
        }
        Long currentTimeInMilisecond=System.currentTimeMillis();
        Long EXPIRATION_TIME_IN_MILISECOND=1L;
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTimeInMilisecond))
                .setExpiration(new Date((currentTimeInMilisecond+EXPIRATION_TIME_IN_MILISECOND)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token){
        Date currentTime=new Date();
        Date expiredTime=extractExpirationTime(token);
        return !currentTime.before(expiredTime);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        if(isTokenExpired(token)){
            return false;
        }
        return true;
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
