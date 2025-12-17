package com.shopEZ.ShopEazzy.security.jwt;

import com.shopEZ.ShopEazzy.security.service.CustomUserDetails;
import com.shopEZ.ShopEazzy.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwtToken = jwtUtils.getTokenFromHeader(request);
            System.out.println("Jwt token: " + jwtToken);
            if(jwtToken != null && jwtUtils.isTokenValid(jwtToken)){
                String userName = jwtUtils.generateUserNameFromToken(jwtToken);
                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService
                        .loadUserByUsername(userName);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("User Name: " + userDetails.getUsername());
                System.out.println("Roles from jwt: " + userDetails.getAuthorities());
            }
        } catch (Exception e) {
            System.out.println("Cannot set user authentication: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
