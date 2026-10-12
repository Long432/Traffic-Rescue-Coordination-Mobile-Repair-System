package com.sosrescuenow.domain.exception;

/** Chuyen trang thai khong hop le theo may trang thai. */
public class TrangThaiKhongHopLeException extends DomainException {

    public TrangThaiKhongHopLeException(String thongBao) {
        super("TRANG_THAI_KHONG_HOP_LE", thongBao);
    }
}
