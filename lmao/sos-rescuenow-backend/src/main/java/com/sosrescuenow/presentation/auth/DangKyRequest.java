package com.sosrescuenow.presentation.auth;

import com.sosrescuenow.domain.model.VaiTro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DangKyRequest(
        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
        String hoTen,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0")
        String soDienThoai,

        // 72 la gioi han cua BCrypt: ky tu thu 73 tro di bi bo qua.
        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự")
        String matKhau,

        @NotNull(message = "Vai trò không được để trống (USER hoặc THO)")
        VaiTro vaiTro) {
}
