package com.swift.security_demo.service.Impl;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.exception.AllException;
import com.swift.security_demo.payload.request.AccessTokenRequest;
import com.swift.security_demo.payload.response.AccessTokenResponse;
import com.swift.security_demo.payload.response.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Your Base64-encoded secret key (must be 256 bits for HS256)
    private static final String SECRET_KEY = "mlgCscmw7roLYm++pnOZlQRh6RY/7jM6F/ekyjRpjn7FaNLD244b9MPPn519YAPgw7ERH/kMcepiOwBDTW2T9w==";

    private static final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000;     // 10 minutes
    private static final long REFRESH_TOKEN_EXPIRATION =  24 * 60 * 60 * 1000; // 1 days


    private SecretKey getSigningKey(){
        byte[] keyBytes  = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(String username) {
        return generateToken(username, ACCESS_TOKEN_EXPIRATION, "access");
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, REFRESH_TOKEN_EXPIRATION, "refresh");
    }

    public String generateToken(String username, Long expirationTime, String tokenType){
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", tokenType);

            return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> (String) claims.get("token_type"));
    }

    private Date extractExpiration(String token) {
        return  extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserEntity userEntity){
        final String username = extractUsername(token);
        return (username.equals(userEntity.getUsername()) && !isTokenExpired(token));
    }

    public ApiResponse regenerateAccessToken(AccessTokenRequest accessTokenRequest) {
        String refreshToken = accessTokenRequest.getRefreshToken();
        if  (isTokenExpired(refreshToken)) {
            throw AllException.builder()
                    .code("JWT004")
                    .status(HttpStatus.GONE)
                    .build();
        }
        String username = extractUsername(refreshToken);
        String newAcessToken = generateAccessToken(username);
        AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
                .username(username)
                .accessToken(newAcessToken)
                .refreshToken(refreshToken)
                .build();
        return new ApiResponse(accessTokenResponse , true , "Access token regenerated");

    }
}
