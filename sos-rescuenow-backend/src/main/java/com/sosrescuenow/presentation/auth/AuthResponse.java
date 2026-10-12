package com.sosrescuenow.presentation.auth;

import com.sosrescuenow.application.auth.AuthResult;
import com.sosrescuenow.domain.model.VaiTro;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds,
                           Integer nguoiDungId, String hoTen, VaiTro vaiTro) {

    public static AuthResponse tu(AuthResult kq) {
        return new AuthResponse(kq.accessToken(), "Bearer", kq.hetHanSauGiay(),
                kq.nguoiDungId(), kq.hoTen(), kq.vaiTro());
    }
}
