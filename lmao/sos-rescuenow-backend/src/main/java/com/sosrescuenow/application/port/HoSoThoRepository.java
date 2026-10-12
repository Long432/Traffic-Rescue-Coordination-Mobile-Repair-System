package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.dieuphoi.UngVienTho;
import com.sosrescuenow.domain.model.HoSoTho;
import com.sosrescuenow.domain.model.ThoTomTat;
import com.sosrescuenow.domain.model.ViTri;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Cong ra: ho so rieng cua tho (trang thai online, ban, diem, loai su co lam duoc). */
public interface HoSoThoRepository {

    /** Tao ho so tho moi: chua online, chua ban, chua duoc admin duyet. */
    void taoHoSoMoi(Integer nguoiDungId);

    Optional<HoSoTho> timHoSo(Integer thoId);

    void datTrangThaiOnline(Integer thoId, boolean online);

    /**
     * Danh dau tho dang ban NEU tho dang san sang (duoc duyet, online, chua ban).
     * Phai la mot thao tac nguyen tu de hai cuoc khong cung gan cho mot tho.
     *
     * @return true neu danh dau duoc, false neu tho khong san sang
     */
    boolean datBanNeuRanh(Integer thoId);

    void datRanh(Integer thoId);

    /** Ghi diem danh gia trung binh (1.00 - 5.00) cua tho. */
    void datDiemTrungBinh(Integer thoId, double diem);

    /** Danh sach tho (moi nhat truoc) kem ten va so dien thoai, de admin duyet. */
    List<ThoTomTat> lietKe(boolean chiChuaDuyet);

    /** Duyet ho so tho va thay danh sach loai su co tho lam duoc bang {@code loaiSuCoIds}. */
    void duyetTho(Integer thoId, Set<Integer> loaiSuCoIds);

    /**
     * Tim tho san sang, lam duoc loai su co, co vi tri moi hon {@code viTriMoiTu} va nam trong
     * khung gan {@code tam}. Tang luu tru co the loc tho (bounding box); khoang cach chinh xac
     * va giai doan do service tinh.
     */
    List<UngVienTho> timUngVienRanh(ViTri tam, double banKinhKm, Integer loaiSuCoId, LocalDateTime viTriMoiTu);
}
