package com.sosrescuenow.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Doc header "Authorization: Bearer <token>". Token hop le thi dat nguoi dung vao SecurityContext;
 * khong hop le thi bo qua, de tang bao mat tra 401 neu endpoint yeu cau dang nhap.
 *
 * Co y KHONG danh dau @Component: bean kieu Filter se bi Spring Boot dang ky them mot lan nua
 * vao chuoi servlet, khien filter chay hai lan. SecurityConfig tu tao doi tuong nay.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TIEN_TO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(TIEN_TO_BEARER)) {
            String token = header.substring(TIEN_TO_BEARER.length()).trim();
            jwtService.xacThuc(token).ifPresent(nguoiDung -> {
                var xacThuc = new UsernamePasswordAuthenticationToken(
                        nguoiDung, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + nguoiDung.vaiTro().name())));
                xacThuc.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(xacThuc);
            });
        }
        chain.doFilter(request, response);
    }
}
