package net.engineeringdigest.journalApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private String SECRET_KEY = "Tak+Hav^uvCHEFsEVfypW#7g9^k*Z8$V";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();  //subject -> username
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());   //if its before curr Date then expired
    }
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {    //all the info content username, subject etc... we put in claims only that goes inside payload
        return Jwts.parser()
                .verifyWith(getSigningKey())   //if this signature is verified can change data in the token and access its info like header ,payload
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims , username);
    }

    private String createToken(Map<String, Object> claims, String subject) {  //subject me username jata
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")  //as we know header takes 2 things the signing algortihm to use to sign with, and type f token
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  //5 mins  (1000ms = 1s) (1000ms*60 = 1min)
                .signWith(getSigningKey())    //getSigningKey gives us the signing algorithm that we sign with  (bydefault we're using -> HMAC SHA256 algorithm)
                .compact();
    }
}
