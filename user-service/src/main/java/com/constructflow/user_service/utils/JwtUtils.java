package com.constructflow.user_service.utils;

import com.constructflow.user_service.entity.CFUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils
{
    private SecretKey key;

    public JwtUtils(@Value("${jwt.secret-key}") String secretKeyString)
    {
        key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKeyString));
    }

    public String getJwtToken(@NonNull CFUser user)
    {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("ROLE", user.getUserRole())
                .claim("ID", user.getId().toString())
                .signWith(key)
                .compact();
    }

    public String verifyJwt(@NonNull String token)
    {
        return null;
    }
}
