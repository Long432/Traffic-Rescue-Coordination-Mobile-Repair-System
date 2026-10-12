package com.sosrescuenow.domain.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaiKhoanBiKhoaException extends DomainException {

    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    /** @param khoaDen thoi diem het khoa (gio UTC), null neu khoa vo thoi han */
    public TaiKhoanBiKhoaException(LocalDateTime khoaDen) {
        super("TAI_KHOAN_BI_KHOA", taoThongBao(khoaDen));
    }

    private static String taoThongBao(LocalDateTime khoaDen) {
        if (khoaDen == null) {
            return "Tài khoản đang bị khóa";
        }
        return "Tài khoản đang bị khóa đến " + DINH_DANG.format(khoaDen) + " (UTC)";
    }
}
