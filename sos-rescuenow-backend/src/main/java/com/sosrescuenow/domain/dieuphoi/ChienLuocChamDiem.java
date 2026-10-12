package com.sosrescuenow.domain.dieuphoi;

/** Strategy: cach cham diem mot ung vien tho. Diem cao hon thi duoc uu tien hon. */
public interface ChienLuocChamDiem {

    double cham(UngVienTho ungVien, double khoangCachKm, double banKinhKm);
}
