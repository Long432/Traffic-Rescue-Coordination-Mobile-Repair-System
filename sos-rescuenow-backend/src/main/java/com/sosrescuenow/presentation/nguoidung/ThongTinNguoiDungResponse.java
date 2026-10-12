package com.sosrescuenow.presentation.nguoidung;

import com.sosrescuenow.application.nguoidung.ThongTinNguoiDung;
import com.sosrescuenow.domain.model.VaiTro;

public record ThongTinNguoiDungResponse(Integer id, String hoTen, String soDienThoai, VaiTro vaiTro) {

    public static ThongTinNguoiDungResponse tu(ThongTinNguoiDung t) {
        return new ThongTinNguoiDungResponse(t.id(), t.hoTen(), t.soDienThoai(), t.vaiTro());
    }
}
