package ru.ntwz.javaspringbootauthexample.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ntwz.javaspringbootauthexample.config.JwtConfig;
import ru.ntwz.javaspringbootauthexample.exception.InvalidTokenException;
import ru.ntwz.javaspringbootauthexample.exception.JwtSignatureException;
import ru.ntwz.javaspringbootauthexample.model.User;
import ru.ntwz.javaspringbootauthexample.service.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private final JwtConfig jwtConfig;

    @Autowired
    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("username", customUserDetails.getUsername());
            claims.put("passwordHash", customUserDetails.getPassword());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Long userId = extractUserId(token);
            String passwordHash = extractPasswordHash(token);

            if (passwordHash == null) {
                return false;
            }

            if (userDetails instanceof User customUserDetails) {
                return userId.equals(customUserDetails.getId())
                        && passwordHash.equals(customUserDetails.getPassword())
                        && !isTokenExpired(token);
            }
            return false;
        } catch (JwtSignatureException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long extractUserId(String token) {
        try {
            Object id = extractClaim(token, claims -> claims.get("id"));
            switch (id) {
                case Integer i -> {
                    return i.longValue();
                }
                case Long l -> {
                    return l;
                }
                case String s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        throw new InvalidTokenException("Invalid id in JWT token: " + id);
                    }
                }
                case null, default -> throw new InvalidTokenException("User id not found in JWT token");
            }
        } catch (JwtSignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("Failed to extract user ID from token: " + e.getMessage(), e);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtConfig.getExpiration() * 1000);

        return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey()).compact();
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtSignatureException e) {
            throw e;
        } catch (Exception e) {
            return true;
        }
    }

    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (JwtSignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("Failed to extract expiration from token: " + e.getMessage(), e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (SignatureException e) {
            throw new JwtSignatureException("JWT signature verification failed. Token may be tampered with or invalid.", e);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
    }

    private String extractPasswordHash(String token) {
        try {
            Object hash = extractClaim(token, claims -> claims.get("passwordHash"));
            return hash != null ? hash.toString() : null;
        } catch (JwtSignatureException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }
}
