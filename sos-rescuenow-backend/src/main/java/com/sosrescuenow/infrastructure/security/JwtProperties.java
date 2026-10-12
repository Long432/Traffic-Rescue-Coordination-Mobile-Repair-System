package com.sosrescuenow.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Cau hinh JWT (prefix "jwt" trong application.yml). Kiem tra ngay luc khoi dong. */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, @DefaultValue("PT24H") Duration expiration) {

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "jwt.secret phải dài ít nhất 32 ký tự. Hãy đặt biến môi trường JWT_SECRET.");
        }
        if (expiration == null || expiration.isZero() || expiration.isNegative()) {
            throw new IllegalStateException("jwt.expiration phải là khoảng thời gian dương (ví dụ PT24H)");
        }
    }
}
