package ifmo.se.coursach_back.security;

import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;
    private final String secretValue;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.secretValue = secret == null ? "" : secret;
        validateSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    private void validateSecret() {
        if (secretValue.isBlank()) {
            throw new IllegalStateException("JWT secret must be provided via security.jwt.secret");
        }
        if (secretValue.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters");
        }
    }

    public String generateToken(Account account) {
        Instant now = Instant.now();
        List<String> roles = account.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .toList();

        return Jwts.builder()
                .subject(account.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }

    public String extractSubject(String token) {
        return parseAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
