package com.sosrescuenow.presentation.auth;

import jakarta.validation.constraints.NotBlank;

public record DangNhapRequest(
        @NotBlank(message = "Số điện thoại không được để trống") String soDienThoai,
        @NotBlank(message = "Mật khẩu không được để trống") String matKhau) {
}
