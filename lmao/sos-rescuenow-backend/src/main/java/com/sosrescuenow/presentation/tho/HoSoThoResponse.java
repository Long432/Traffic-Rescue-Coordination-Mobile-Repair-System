package com.sosrescuenow.presentation.tho;

import com.sosrescuenow.domain.model.HoSoTho;

public record HoSoThoResponse(boolean online, boolean dangBan, boolean daDuyet,
                              double diemTrungBinh, double tiLeNhanCuoc) {

    public static HoSoThoResponse tu(HoSoTho h) {
        return new HoSoThoResponse(h.online(), h.dangBan(), h.daDuyet(), h.diemTrungBinh(), h.tiLeNhanCuoc());
    }
}
