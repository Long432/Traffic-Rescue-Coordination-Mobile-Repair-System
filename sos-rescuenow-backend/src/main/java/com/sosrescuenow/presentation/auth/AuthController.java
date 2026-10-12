package com.sosrescuenow.presentation.auth;

import com.sosrescuenow.application.auth.AuthService;
import com.sosrescuenow.application.auth.DangKyCommand;
import com.sosrescuenow.application.auth.DangNhapCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/dang-ky")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse dangKy(@Valid @RequestBody DangKyRequest request) {
        return AuthResponse.tu(authService.dangKy(
                new DangKyCommand(request.hoTen(), request.soDienThoai(), request.matKhau(), request.vaiTro())));
    }

    @PostMapping("/dang-nhap")
    public AuthResponse dangNhap(@Valid @RequestBody DangNhapRequest request) {
        return AuthResponse.tu(authService.dangNhap(
                new DangNhapCommand(request.soDienThoai(), request.matKhau())));
    }
}
