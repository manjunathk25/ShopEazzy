package com.shopEZ.ShopEazzy.security.jwt;

import com.shopEZ.ShopEazzy.security.service.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Long EXPIRATION_IN_MS;
    private final String SECRET_KEY;
    private final String jwtCookie;

    public JwtUtils(@Value("${app.jwt.expiration}") Long EXPIRATION_IN_MS,
                    @Value("${app.jwt.secret}") String SECRET_KEY,
                    @Value("${app.jwt.cookie}") String jwtCookie) {
        this.EXPIRATION_IN_MS = EXPIRATION_IN_MS;
        this.SECRET_KEY = SECRET_KEY;
        this.jwtCookie = jwtCookie;
    }

    public String getTokenFromCookie(HttpServletRequest request){

        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        return cookie != null ?
                cookie.getValue() :
                null;
    }

    public ResponseCookie generateJwtCookie(CustomUserDetails customUserDetails){

        String jwt = generateTokenFromUserName(customUserDetails.getUsername());
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .build();
    }

    public ResponseCookie cleanJwtCookie(){
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
    }

    public String generateTokenFromUserName(String userName){

        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + EXPIRATION_IN_MS))
                .signWith(key())
                .compact();
    }

    public String generateUserNameFromToken(String jwtToken){

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(jwtToken)
                .getPayload().getSubject();
    }

    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET_KEY)
        );
    }

    public boolean isTokenValid(String jwtToken){

        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build().parseSignedClaims(jwtToken)
                    .getPayload();
            return true;
        } catch (MalformedJwtException e){
            System.out.println("Invalid jwt token: " + e.getMessage());
        } catch (ExpiredJwtException e){
            System.out.println("Jwt token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e){
            System.out.println("Jwt token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println("Jwt claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
