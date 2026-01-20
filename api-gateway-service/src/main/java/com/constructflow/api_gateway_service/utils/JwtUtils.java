package com.constructflow.api_gateway_service.utils;

import com.constructflow.api_gateway_service.dto.JwtPayloadDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
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

    public JwtPayloadDto verifyJwt(@NonNull String token)
    {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return JwtPayloadDto.builder()
                .username(claims.getSubject())
                .userId(Long.decode((String) claims.get("ID")))
                .userRole((String) claims.get("ROLE"))
                .build();
    }
}
