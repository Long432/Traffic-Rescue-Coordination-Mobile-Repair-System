package com.sosrescuenow.infrastructure.security;

import com.sosrescuenow.application.port.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String bam(String matKhauGoc) {
        return passwordEncoder.encode(matKhauGoc);
    }

    @Override
    public boolean khop(String matKhauGoc, String matKhauDaBam) {
        return passwordEncoder.matches(matKhauGoc, matKhauDaBam);
    }
}
