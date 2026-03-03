package com.gutti.store.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // This should be stored securely in application properties
    private static final String SECRET_KEY = "z$C&F)J@NcRfUjXn2r5u8x/A?D(G-KaPdSgVkYp3s6v9y$B&E)H+MbQeThWmZq4t";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 24 hours
                .sign(algorithm);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(userDetails.getUsername())
                    .build();
            verifier.verify(token); // This will throw an exception if not valid
            return true;
        } catch (Exception exception){
            return false;
        }
    }
}