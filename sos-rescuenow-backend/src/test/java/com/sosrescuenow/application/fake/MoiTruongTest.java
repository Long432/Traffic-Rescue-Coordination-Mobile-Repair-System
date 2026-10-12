package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.admin.AdminService;
import com.sosrescuenow.application.cuoc.CuocService;
import com.sosrescuenow.application.cuoc.ThoCuocService;
import com.sosrescuenow.application.cuoc.TimThoService;
import com.sosrescuenow.application.danhgia.DanhGiaService;
import com.sosrescuenow.domain.dieuphoi.ChamDiemTongHop;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.LoaiSuCo;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.domain.model.ViTri;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Moi truong dung san cho test: bo nho thay CSDL, dong ho co dinh.
 * BAY_GIO = 03:00 UTC = 10:00 gio Viet Nam (ban ngay, khong tinh phu phi ban dem).
 */
public class MoiTruongTest {

    public static final LocalDateTime BAY_GIO = LocalDateTime.of(2026, 10, 5, 3, 0, 0);
    public static final ViTri DIEM_KHACH = new ViTri(10.7769, 106.7009);
    public static final int LOAI_THUNG_LOP = 1;
    public static final int LOAI_HET_XANG = 2;
    public static final int KHACH_AN = 100;

    public final Clock clock = Clock.fixed(BAY_GIO.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    public final InMemoryViTriThoRepository viTriRepo = new InMemoryViTriThoRepository();
    public final InMemoryHoSoThoRepository hoSoRepo = new InMemoryHoSoThoRepository(viTriRepo);
    public final InMemoryCuocCuuHoRepository cuocRepo = new InMemoryCuocCuuHoRepository();
    public final InMemoryNguoiDungRepository nguoiDungRepo = new InMemoryNguoiDungRepository();
    public final InMemoryDanhGiaRepository danhGiaRepo = new InMemoryDanhGiaRepository(cuocRepo);
    public final InMemoryLichSuTrangThaiRepository lichSu = new InMemoryLichSuTrangThaiRepository();
    public final InMemoryLoaiSuCoRepository loaiRepo = new InMemoryLoaiSuCoRepository(
            new LoaiSuCo(LOAI_THUNG_LOP, "Thủng lốp", new BigDecimal("80000"), 2),
            new LoaiSuCo(LOAI_HET_XANG, "Hết xăng", new BigDecimal("60000"), 2));
    public final TimThoService timThoService = new TimThoService(hoSoRepo, new ChamDiemTongHop());
    public final ThoCuocService thoService =
            new ThoCuocService(hoSoRepo, viTriRepo, cuocRepo, lichSu, timThoService, clock);
    public final CuocService cuocService =
            new CuocService(loaiRepo, cuocRepo, lichSu, hoSoRepo, nguoiDungRepo, viTriRepo, timThoService, clock);

    public final DanhGiaService danhGiaService = new DanhGiaService(cuocRepo, danhGiaRepo, hoSoRepo);
    public final AdminService adminService = new AdminService(hoSoRepo, loaiRepo);

    /** Tho (da san sang) nhan va hoan thanh tron mot cuoc cua {@code khachId}; tra ve cuoc da HOAN_THANH. */
    public CuocCuuHo hoanThanhCuoc(int thoId, int khachId) {
        CuocCuuHo cuoc = themCuocDangTim(khachId, 30);
        thoService.nhanCuoc(thoId, cuoc.getId());
        thoService.chuyenTrangThai(thoId, cuoc.getId(), TrangThaiCuoc.DANG_DEN);
        thoService.capNhatViTri(thoId, DIEM_KHACH.latitude(), DIEM_KHACH.longitude());
        thoService.chuyenTrangThai(thoId, cuoc.getId(), TrangThaiCuoc.DA_DEN);
        thoService.chuyenTrangThai(thoId, cuoc.getId(), TrangThaiCuoc.DANG_SUA);
        thoService.chuyenTrangThai(thoId, cuoc.getId(), TrangThaiCuoc.CHO_THANH_TOAN);
        thoService.chuyenTrangThai(thoId, cuoc.getId(), TrangThaiCuoc.HOAN_THANH);
        return cuocRepo.timTheoId(cuoc.getId()).orElseThrow();
    }

    /** Diem cach DIEM_KHACH khoang {@code km} ve phia bac (1 do vi do ~ 111 km). */
    public static ViTri cachKhachKm(double km) {
        return new ViTri(DIEM_KHACH.latitude() + km / 111.0, DIEM_KHACH.longitude());
    }

    /** Them mot tho san sang (online, ranh, da duyet, lam duoc THUNG_LOP) co vi tri moi nhat tai {@code viTri}. */
    public void themThoSanSang(int id, ViTri viTri, double sao, double tiLe) {
        hoSoRepo.them(id, true, false, true, sao, tiLe, LOAI_THUNG_LOP);
        viTriRepo.luu(id, viTri, BAY_GIO);
    }

    /** Them mot cuoc DANG_TIM da duoc tim {@code tuoiGiay} giay. */
    public CuocCuuHo themCuocDangTim(int khachId, long tuoiGiay) {
        LocalDateTime batDau = BAY_GIO.minusSeconds(tuoiGiay);
        return cuocRepo.them(CuocCuuHo.moi(khachId, LOAI_THUNG_LOP, DIEM_KHACH, null, null,
                new BigDecimal("80000"), batDau));
    }

    public TrangThaiCuoc trangThaiCuoc(Long id) {
        return cuocRepo.timTheoId(id).orElseThrow().getTrangThai();
    }
}
