package com.sosrescuenow.presentation.tho;

import com.sosrescuenow.application.cuoc.CuocQuanhToi;
import com.sosrescuenow.presentation.cuoc.CuocResponse;

public record CuocQuanhToiResponse(CuocResponse cuoc, double khoangCachKm) {

    public static CuocQuanhToiResponse tu(CuocQuanhToi c) {
        return new CuocQuanhToiResponse(CuocResponse.tu(c.cuoc()), Math.round(c.khoangCachKm() * 100.0) / 100.0);
    }
}
