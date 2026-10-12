package com.sosrescuenow.domain.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Vong doi cua mot cuoc cuu ho (may trang thai).
 * Ten phai khop CHECK constraint cua cot CuocCuuHo.TrangThai.
 *
 * <pre>
 * DANG_TIM -> DA_NHAN | KHONG_CO_THO | HUY
 * DA_NHAN  -> DANG_DEN | DANG_TIM (tho huy, tim tho khac) | HUY
 * DANG_DEN -> DA_DEN | DANG_TIM (tho tra cuoc) | HUY
 * DA_DEN   -> DANG_SUA | HUY
 * DANG_SUA -> CHO_THANH_TOAN
 * CHO_THANH_TOAN -> HOAN_THANH
 * HOAN_THANH, HUY, KHONG_CO_THO: trang thai ket thuc
 * </pre>
 */
public enum TrangThaiCuoc {
    DANG_TIM,
    DA_NHAN,
    DANG_DEN,
    DA_DEN,
    DANG_SUA,
    CHO_THANH_TOAN,
    HOAN_THANH,
    HUY,
    KHONG_CO_THO;

    /** Cac trang thai hop le co the chuyen sang tu trang thai hien tai (tap chi doc). */
    public Set<TrangThaiCuoc> cacTrangThaiTiepTheo() {
        EnumSet<TrangThaiCuoc> tiepTheo = switch (this) {
            case DANG_TIM -> EnumSet.of(DA_NHAN, KHONG_CO_THO, HUY);
            case DA_NHAN -> EnumSet.of(DANG_DEN, DANG_TIM, HUY);
            case DANG_DEN -> EnumSet.of(DA_DEN, DANG_TIM, HUY);
            case DA_DEN -> EnumSet.of(DANG_SUA, HUY);
            case DANG_SUA -> EnumSet.of(CHO_THANH_TOAN);
            case CHO_THANH_TOAN -> EnumSet.of(HOAN_THANH);
            case HOAN_THANH, HUY, KHONG_CO_THO -> EnumSet.noneOf(TrangThaiCuoc.class);
        };
        return Collections.unmodifiableSet(tiepTheo);
    }

    public boolean coTheChuyenSang(TrangThaiCuoc dich) {
        return cacTrangThaiTiepTheo().contains(dich);
    }

    public boolean laKetThuc() {
        return cacTrangThaiTiepTheo().isEmpty();
    }
}
