package com.sosrescuenow.infrastructure.security;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.port.TokenIssuer;
import com.sosrescuenow.domain.model.VaiTro;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Phat hanh va xac thuc JWT (HMAC). Token chi chua id va vai tro, khong chua du lieu nhay cam. */
@Component
public class JwtService implements TokenIssuer {

    private static final String CLAIM_VAI_TRO = "role";

    private final SecretKey khoa;
    private final Duration thoiHan;
    private final Clock clock;

    public JwtService(JwtProperties properties, Clock clock) {
        this.khoa = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.thoiHan = properties.expiration();
        this.clock = clock;
    }

    @Override
    public String phatHanh(Integer nguoiDungId, VaiTro vaiTro) {
        Instant bayGio = clock.instant();
        return Jwts.builder()
                .subject(String.valueOf(nguoiDungId))
                .claim(CLAIM_VAI_TRO, vaiTro.name())
                .issuedAt(Date.from(bayGio))
                .expiration(Date.from(bayGio.plus(thoiHan)))
                .signWith(khoa)
                .compact();
    }

    @Override
    public Duration thoiHan() {
        return thoiHan;
    }

    /** @return nguoi dung neu token hop le va chua het han, nguoc lai rong */
    public Optional<AuthenticatedUser> xacThuc(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(khoa)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Integer id = Integer.valueOf(claims.getSubject());
            VaiTro vaiTro = VaiTro.valueOf(claims.get(CLAIM_VAI_TRO, String.class));
            return Optional.of(new AuthenticatedUser(id, vaiTro));
        } catch (JwtException | IllegalArgumentException e) {
            // Token sai chu ky, het han, sai dinh dang, hoac claim khong hop le.
            return Optional.empty();
        }
    }
}
