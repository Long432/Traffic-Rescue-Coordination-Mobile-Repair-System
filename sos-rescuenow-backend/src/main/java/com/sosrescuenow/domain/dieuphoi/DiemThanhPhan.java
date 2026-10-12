package com.sosrescuenow.domain.dieuphoi;

/** Cac phep chuan hoa ve thang [0, 1] dung chung cho cac chien luoc cham diem. */
final class DiemThanhPhan {

    private DiemThanhPhan() {
    }

    static double gan(double khoangCachKm, double banKinhKm) {
        if (banKinhKm <= 0) {
            return 0;
        }
        return 1.0 - kep(khoangCachKm / banKinhKm);
    }

    static double kep(double giaTri) {
        return Math.max(0.0, Math.min(1.0, giaTri));
    }
}
