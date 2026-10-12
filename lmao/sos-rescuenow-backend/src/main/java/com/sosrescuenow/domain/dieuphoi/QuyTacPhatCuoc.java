package com.sosrescuenow.domain.dieuphoi;

import java.time.Duration;

/**
 * Quy tac "phat cuoc" theo thoi gian da tim (khong can luu them trang thai nao):
 * <pre>
 *   0s  - 20s : chi 3 tho xep hang cao nhat trong 5 km duoc thay/nhan cuoc
 *   20s - 60s : moi tho ranh trong 5 km
 *   60s - 120s: mo rong ra 10 km
 *   tu 120s   : het han, cuoc chuyen sang KHONG_CO_THO
 * </pre>
 */
public final class QuyTacPhatCuoc {

    public static final Duration THOI_GIAN_UU_TIEN = Duration.ofSeconds(20);
    public static final Duration THOI_GIAN_MO_RONG = Duration.ofSeconds(60);
    public static final Duration THOI_GIAN_HET_HAN = Duration.ofSeconds(120);
    /** Vi tri cua tho cu hon moc nay duoc coi la mat tin hieu. */
    public static final Duration THOI_GIAN_VI_TRI_HOP_LE = Duration.ofMinutes(5);

    public static final double BAN_KINH_CO_BAN_KM = 5.0;
    public static final double BAN_KINH_MO_RONG_KM = 10.0;
    public static final int SO_THO_UU_TIEN = 3;

    private QuyTacPhatCuoc() {
    }

    /** @param soThoUuTien so tho duoc uu tien (null = khong gioi han) */
    public record GiaiDoan(double banKinhKm, Integer soThoUuTien, boolean hetHan) {
    }

    public static GiaiDoan tai(Duration tuoiTimKiem) {
        if (tuoiTimKiem.compareTo(THOI_GIAN_HET_HAN) >= 0) {
            return new GiaiDoan(BAN_KINH_MO_RONG_KM, null, true);
        }
        if (tuoiTimKiem.compareTo(THOI_GIAN_MO_RONG) >= 0) {
            return new GiaiDoan(BAN_KINH_MO_RONG_KM, null, false);
        }
        if (tuoiTimKiem.compareTo(THOI_GIAN_UU_TIEN) >= 0) {
            return new GiaiDoan(BAN_KINH_CO_BAN_KM, null, false);
        }
        return new GiaiDoan(BAN_KINH_CO_BAN_KM, SO_THO_UU_TIEN, false);
    }
}
