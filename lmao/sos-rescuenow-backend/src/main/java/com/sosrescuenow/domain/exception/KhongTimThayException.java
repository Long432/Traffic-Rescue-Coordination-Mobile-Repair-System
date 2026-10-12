package com.sosrescuenow.domain.exception;

/** Khong tim thay doi tuong can tim. */
public class KhongTimThayException extends DomainException {

    public KhongTimThayException(String thongBao) {
        super("KHONG_TIM_THAY", thongBao);
    }
}
