package com.sosrescuenow.domain.exception;

/** Du lieu dau vao khong hop le. */
public class DuLieuKhongHopLeException extends DomainException {

    public DuLieuKhongHopLeException(String thongBao) {
        super("DU_LIEU_KHONG_HOP_LE", thongBao);
    }
}
