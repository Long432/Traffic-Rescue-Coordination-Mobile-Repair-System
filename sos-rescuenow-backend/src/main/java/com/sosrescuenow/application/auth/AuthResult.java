package com.sosrescuenow.application.auth;

import com.sosrescuenow.domain.model.VaiTro;

public record AuthResult(String accessToken, long hetHanSauGiay, Integer nguoiDungId, String hoTen, VaiTro vaiTro) {
}
