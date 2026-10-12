package com.sosrescuenow.domain.exception;

/** Khong du quyen thuc hien thao tac tren doi tuong nay. */
public class KhongCoQuyenException extends DomainException {

    public KhongCoQuyenException(String thongBao) {
        super("KHONG_CO_QUYEN", thongBao);
    }
}
