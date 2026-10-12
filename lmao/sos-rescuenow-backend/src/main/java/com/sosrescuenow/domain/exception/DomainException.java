package com.sosrescuenow.domain.exception;

/** Goc cua moi loi nghiep vu. Tang web anh xa ra ma HTTP, domain khong biet gi ve HTTP. */
public abstract class DomainException extends RuntimeException {

    private final String maLoi;

    protected DomainException(String maLoi, String thongBao) {
        super(thongBao);
        this.maLoi = maLoi;
    }

    public String getMaLoi() {
        return maLoi;
    }
}
