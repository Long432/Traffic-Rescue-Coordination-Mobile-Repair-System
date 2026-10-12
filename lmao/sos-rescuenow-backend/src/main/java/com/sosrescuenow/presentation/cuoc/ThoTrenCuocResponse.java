package com.sosrescuenow.presentation.cuoc;

import com.sosrescuenow.application.cuoc.ThoTrenCuoc;

import java.time.Instant;
import java.time.ZoneOffset;

/** latitude/longitude/capNhatLuc la null neu tho chua gui vi tri. */
public record ThoTrenCuocResponse(Integer thoId, String hoTen, Double latitude, Double longitude, Instant capNhatLuc) {

    public static ThoTrenCuocResponse tu(ThoTrenCuoc t) {
        return new ThoTrenCuocResponse(t.thoId(), t.hoTen(),
                t.viTri() == null ? null : t.viTri().latitude(),
                t.viTri() == null ? null : t.viTri().longitude(),
                t.capNhatLuc() == null ? null : t.capNhatLuc().toInstant(ZoneOffset.UTC));
    }
}
