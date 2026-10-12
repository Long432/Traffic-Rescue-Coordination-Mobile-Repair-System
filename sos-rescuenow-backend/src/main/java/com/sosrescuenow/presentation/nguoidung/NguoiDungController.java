package com.sosrescuenow.presentation.nguoidung;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.nguoidung.NguoiDungService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    /** Thong tin nguoi dung dang dang nhap (dung de thu token). */
    @GetMapping
    public ThongTinNguoiDungResponse toi(@AuthenticationPrincipal AuthenticatedUser nguoiDung) {
        return ThongTinNguoiDungResponse.tu(nguoiDungService.layThongTin(nguoiDung.nguoiDungId()));
    }
}
