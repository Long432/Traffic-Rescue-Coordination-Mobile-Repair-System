package com.sosrescuenow.domain.dieuphoi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Bang gia: gia co ban cua loai su co, cong phu phi ban dem 20% (22:00 - 05:00 gio Viet Nam). */
public final class BangGia {

    private static final ZoneId MUI_GIO_VIET_NAM = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final LocalTime BAT_DAU_BAN_DEM = LocalTime.of(22, 0);
    private static final LocalTime KET_THUC_BAN_DEM = LocalTime.of(5, 0);
    private static final BigDecimal HE_SO_BAN_DEM = new BigDecimal("1.20");
    private static final BigDecimal NGHIN = BigDecimal.valueOf(1000);

    private BangGia() {
    }

    /** @param bayGioUtc thoi diem hien tai theo gio UTC */
    public static BigDecimal tinhGia(BigDecimal giaCoBan, LocalDateTime bayGioUtc) {
        LocalTime gioVietNam = bayGioUtc.atOffset(ZoneOffset.UTC).atZoneSameInstant(MUI_GIO_VIET_NAM).toLocalTime();
        boolean banDem = !gioVietNam.isBefore(BAT_DAU_BAN_DEM) || gioVietNam.isBefore(KET_THUC_BAN_DEM);
        BigDecimal gia = banDem ? giaCoBan.multiply(HE_SO_BAN_DEM) : giaCoBan;
        return gia.divide(NGHIN, 0, RoundingMode.HALF_UP).multiply(NGHIN);
    }
}
