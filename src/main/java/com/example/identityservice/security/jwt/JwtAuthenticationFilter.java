package com.example.identityservice.security.jwt;

import com.example.identityservice.security.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        System.out.println("==================== JWT FILTER DEBUG ====================");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Servlet Path: '" + path + "'");

        // 🔹 Auth və public endpointlər üçün JWT yoxlamasını keç
        if (path.startsWith("/api/v1/auth") || path.startsWith("/api/v1/test/public")) {
            System.out.println(">>> Public və ya Auth endpoint — token yoxlanılmır.");
            System.out.println("==========================================================");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("<<< Token yoxlanmasına başlanılır...");

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String email = null;
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                email = jwtUtil.extractEmail(token);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT expired");
            } catch (Exception e) {
                logger.warn("Invalid JWT");
            }
        } else {
            System.out.println("<<< Authorization header tapılmadı və ya 'Bearer ' ilə başlamır.");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        System.out.println("==========================================================");
        filterChain.doFilter(request, response);
    }
}
