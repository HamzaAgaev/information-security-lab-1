package info.sec.lab1.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final long accessTokenExpirationMs = 15*60*1000L; // 15 minutes
    private static final long refreshTokenExpirationMs = 30*24*60*60*1000L; // 30 days

    @Value("${jwt.access-secret}")
    private String accessSecret;
    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    public JwtAuthenticationToken generateJwtAuthenticationToken(UserDetails userDetails) {
        return generateJwtAuthenticationToken(Map.of(), userDetails);
    }

    public JwtAuthenticationToken generateJwtAuthenticationToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        String accessToken = generateToken(JwtTokenType.ACCESS, extraClaims, userDetails);
        String refreshToken = generateToken(JwtTokenType.REFRESH, extraClaims, userDetails);
        return new JwtAuthenticationToken(accessToken, refreshToken, userDetails.getUsername(), userDetails.getAuthorities());
    }

    private String generateToken(
            JwtTokenType tokenType,
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        long expirationMs = tokenType == JwtTokenType.ACCESS
                ? accessTokenExpirationMs
                : refreshTokenExpirationMs;

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expirationMs)))
                .signWith(getSignKey(tokenType))
                .compact();
    }

    public boolean isTokenValid(String token, JwtTokenType tokenType, UserDetails userDetails) {
        String username = extractUsername(token, tokenType);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenType);
    }

    public String extractUsername(JwtAuthenticationToken jwtAuthenticationToken) {
        return extractUsername(jwtAuthenticationToken.getAccessToken(), JwtTokenType.ACCESS);
    }

    public boolean isTokenExpired(String token, JwtTokenType tokenType) {
        try {
            return extractExpiration(token, tokenType).before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public String extractUsername(String token, JwtTokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getSubject);
    }

    private Date extractExpiration(String token, JwtTokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getExpiration);
    }

    private <T> T extractClaim(
            String token,
            JwtTokenType tokenType,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, JwtTokenType tokenType) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey(tokenType))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignKey(JwtTokenType tokenType) {
        String secret = tokenType == JwtTokenType.ACCESS ? accessSecret : refreshSecret;
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
