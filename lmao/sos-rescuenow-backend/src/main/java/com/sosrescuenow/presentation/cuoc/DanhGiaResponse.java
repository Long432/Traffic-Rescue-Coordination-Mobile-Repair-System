package com.sosrescuenow.presentation.cuoc;

import com.sosrescuenow.domain.model.DanhGia;

public record DanhGiaResponse(Long cuocId, int soSao, String nhanXet) {

    public static DanhGiaResponse tu(DanhGia d) {
        return new DanhGiaResponse(d.cuocId(), d.soSao(), d.nhanXet());
    }
}
