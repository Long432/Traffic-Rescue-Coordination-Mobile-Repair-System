package com.sosrescuenow.presentation.admin;

import com.sosrescuenow.domain.model.ThoTomTat;

public record ThoTomTatResponse(Integer id, String hoTen, String soDienThoai, boolean daDuyet,
                                boolean online, boolean dangBan, double diemTrungBinh) {

    public static ThoTomTatResponse tu(ThoTomTat t) {
        return new ThoTomTatResponse(t.id(), t.hoTen(), t.soDienThoai(), t.daDuyet(), t.online(), t.dangBan(),
                t.diemTrungBinh());
    }
}
