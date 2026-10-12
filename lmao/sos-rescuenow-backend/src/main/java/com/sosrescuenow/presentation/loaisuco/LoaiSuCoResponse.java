package com.sosrescuenow.presentation.loaisuco;

import com.sosrescuenow.domain.model.LoaiSuCo;

import java.math.BigDecimal;

public record LoaiSuCoResponse(Integer id, String ten, BigDecimal giaCoBan, int mucKhanCap) {

    public static LoaiSuCoResponse tu(LoaiSuCo l) {
        return new LoaiSuCoResponse(l.id(), l.ten(), l.giaCoBan(), l.mucKhanCap());
    }
}
