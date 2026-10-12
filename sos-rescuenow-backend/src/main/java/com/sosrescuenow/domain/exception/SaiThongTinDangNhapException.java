package com.sosrescuenow.domain.exception;

/**
 * Sai so dien thoai hoac mat khau. Co y dung CHUNG mot thong bao cho ca hai truong hop
 * de khong lo viec so dien thoai nao da ton tai.
 */
public class SaiThongTinDangNhapException extends DomainException {

    public SaiThongTinDangNhapException() {
        super("SAI_THONG_TIN_DANG_NHAP", "Số điện thoại hoặc mật khẩu không đúng");
    }
}
