package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.domain.model.ViTri;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Chuyen doi toa do giua domain (double) va cot DECIMAL(9,6) cua CSDL. */
final class ToaDo {

    private ToaDo() {
    }

    static BigDecimal sangDecimal(double giaTri) {
        return BigDecimal.valueOf(giaTri).setScale(6, RoundingMode.HALF_UP);
    }

    static ViTri sangViTri(BigDecimal latitude, BigDecimal longitude) {
        return new ViTri(latitude.doubleValue(), longitude.doubleValue());
    }
}
