package com.sosrescuenow.domain.exception;

/** Du lieu bi trung (vi du so dien thoai da ton tai). */
public class TrungLapException extends DomainException {

    public TrungLapException(String thongBao) {
        super("TRUNG_LAP", thongBao);
    }
}
