package com.sosrescuenow.domain.dieuphoi;

/**
 * Diem = 0.5 x do gan + 0.3 x (danh gia / 5) + 0.2 x ti le nhan cuoc.
 * (Thanh phan "khop loai dich vu" khong can, vi tho khong lam duoc loai su co da bi loai tu dau.)
 */
public class ChamDiemTongHop implements ChienLuocChamDiem {

    private static final double TRONG_SO_GAN = 0.5;
    private static final double TRONG_SO_DANH_GIA = 0.3;
    private static final double TRONG_SO_TI_LE_NHAN = 0.2;

    @Override
    public double cham(UngVienTho ungVien, double khoangCachKm, double banKinhKm) {
        return TRONG_SO_GAN * DiemThanhPhan.gan(khoangCachKm, banKinhKm)
                + TRONG_SO_DANH_GIA * DiemThanhPhan.kep(ungVien.diemTrungBinh() / 5.0)
                + TRONG_SO_TI_LE_NHAN * DiemThanhPhan.kep(ungVien.tiLeNhanCuoc());
    }
}
