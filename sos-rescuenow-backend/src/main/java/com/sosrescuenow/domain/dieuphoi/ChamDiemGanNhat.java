package com.sosrescuenow.domain.dieuphoi;

/** Chien luoc don gian: tho cang gan diem cang cao. */
public class ChamDiemGanNhat implements ChienLuocChamDiem {

    @Override
    public double cham(UngVienTho ungVien, double khoangCachKm, double banKinhKm) {
        return DiemThanhPhan.gan(khoangCachKm, banKinhKm);
    }
}
