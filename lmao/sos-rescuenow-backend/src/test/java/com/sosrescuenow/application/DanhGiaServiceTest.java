package com.sosrescuenow.application;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.fake.MoiTruongTest;
import com.sosrescuenow.domain.dieuphoi.UngVienDaXepHang;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.DanhGia;
import com.sosrescuenow.domain.model.VaiTro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sosrescuenow.application.fake.MoiTruongTest.DIEM_KHACH;
import static com.sosrescuenow.application.fake.MoiTruongTest.KHACH_AN;
import static com.sosrescuenow.application.fake.MoiTruongTest.LOAI_THUNG_LOP;
import static com.sosrescuenow.application.fake.MoiTruongTest.cachKhachKm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DanhGiaServiceTest {

    private MoiTruongTest mt;

    @BeforeEach
    void setUp() {
        mt = new MoiTruongTest();
        mt.themThoSanSang(1, cachKhachKm(0.5), 5.0, 1.0);
    }

    private double diemTho(int thoId) {
        return mt.hoSoRepo.timHoSo(thoId).orElseThrow().diemTrungBinh();
    }

    @Test
    void danhGia_capNhatDiemTrungBinhCuaTho() {
        CuocCuuHo c1 = mt.hoanThanhCuoc(1, KHACH_AN);
        mt.danhGiaService.danhGia(c1.getId(), KHACH_AN, 4, "Nhanh");
        assertThat(diemTho(1)).isEqualTo(4.0);

        CuocCuuHo c2 = mt.hoanThanhCuoc(1, KHACH_AN + 1);
        mt.danhGiaService.danhGia(c2.getId(), KHACH_AN + 1, 5, null);
        assertThat(diemTho(1)).isEqualTo(4.5);

        CuocCuuHo c3 = mt.hoanThanhCuoc(1, KHACH_AN + 2);
        mt.danhGiaService.danhGia(c3.getId(), KHACH_AN + 2, 2, null);
        assertThat(diemTho(1)).isEqualTo(3.67);   // (4 + 5 + 2) / 3 lam tron 2 chu so
    }

    @Test
    void danhGia_nhanXetTrangChuyenThanhNull_vaTrimChuoi() {
        CuocCuuHo c1 = mt.hoanThanhCuoc(1, KHACH_AN);
        DanhGia d = mt.danhGiaService.danhGia(c1.getId(), KHACH_AN, 5, "   ");
        assertThat(d.nhanXet()).isNull();

        CuocCuuHo c2 = mt.hoanThanhCuoc(1, KHACH_AN + 1);
        assertThat(mt.danhGiaService.danhGia(c2.getId(), KHACH_AN + 1, 5, "  Tot  ").nhanXet()).isEqualTo("Tot");
    }

    @Test
    void danhGia_soSaoNgoaiKhoang_biTuChoi() {
        CuocCuuHo c = mt.hoanThanhCuoc(1, KHACH_AN);
        assertThatThrownBy(() -> mt.danhGiaService.danhGia(c.getId(), KHACH_AN, 0, null))
                .isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> mt.danhGiaService.danhGia(c.getId(), KHACH_AN, 6, null))
                .isInstanceOf(DuLieuKhongHopLeException.class);
        assertThat(mt.danhGiaRepo.timTheoCuoc(c.getId()).isEmpty()).isTrue();
    }

    @Test
    void danhGia_cuocChuaHoanThanh_biTuChoi() {
        CuocCuuHo c = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, c.getId());
        assertThatThrownBy(() -> mt.danhGiaService.danhGia(c.getId(), KHACH_AN, 5, null))
                .isInstanceOf(TrangThaiKhongHopLeException.class);
    }

    @Test
    void danhGia_khongPhaiChuCuoc_biTuChoi() {
        CuocCuuHo c = mt.hoanThanhCuoc(1, KHACH_AN);
        assertThatThrownBy(() -> mt.danhGiaService.danhGia(c.getId(), KHACH_AN + 1, 5, null))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void danhGia_haiLan_biTuChoiVaDiemKhongDoi() {
        CuocCuuHo c = mt.hoanThanhCuoc(1, KHACH_AN);
        mt.danhGiaService.danhGia(c.getId(), KHACH_AN, 3, null);

        assertThatThrownBy(() -> mt.danhGiaService.danhGia(c.getId(), KHACH_AN, 5, null))
                .isInstanceOf(XungDotException.class);
        assertThat(diemTho(1)).isEqualTo(3.0);
    }

    @Test
    void danhGia_cuocKhongTonTai() {
        assertThatThrownBy(() -> mt.danhGiaService.danhGia(999L, KHACH_AN, 5, null))
                .isInstanceOf(KhongTimThayException.class);
    }

    @Test
    void xemDanhGia_phanQuyen() {
        CuocCuuHo c = mt.hoanThanhCuoc(1, KHACH_AN);
        assertThat(mt.danhGiaService.xemDanhGia(c.getId(), new AuthenticatedUser(KHACH_AN, VaiTro.USER)).isEmpty()).isTrue();

        mt.danhGiaService.danhGia(c.getId(), KHACH_AN, 4, "Ok");
        assertThat(mt.danhGiaService.xemDanhGia(c.getId(), new AuthenticatedUser(KHACH_AN, VaiTro.USER)).orElseThrow().soSao()).isEqualTo(4);
        assertThat(mt.danhGiaService.xemDanhGia(c.getId(), new AuthenticatedUser(1, VaiTro.THO)).isPresent()).isTrue();
        assertThat(mt.danhGiaService.xemDanhGia(c.getId(), new AuthenticatedUser(7, VaiTro.ADMIN)).isPresent()).isTrue();
        assertThatThrownBy(() -> mt.danhGiaService.xemDanhGia(c.getId(), new AuthenticatedUser(KHACH_AN + 1, VaiTro.USER)))
                .isInstanceOf(KhongCoQuyenException.class);
        assertThatThrownBy(() -> mt.danhGiaService.xemDanhGia(c.getId(), new AuthenticatedUser(2, VaiTro.THO)))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void diemDanhGiaAnhHuongThuTuXepHang() {
        mt.themThoSanSang(2, cachKhachKm(0.5), 5.0, 1.0);
        // hai tho cung cho, cung chi so ban dau; moi tho hoan thanh mot cuoc
        CuocCuuHo cuocCuaTho1 = mt.hoanThanhCuoc(1, KHACH_AN);
        CuocCuuHo cuocCuaTho2 = mt.hoanThanhCuoc(2, KHACH_AN + 1);
        mt.danhGiaService.danhGia(cuocCuaTho1.getId(), KHACH_AN, 1, "Cham");        // tho 1: 1 sao
        mt.danhGiaService.danhGia(cuocCuaTho2.getId(), KHACH_AN + 1, 5, "Tuyet");   // tho 2: 5 sao

        List<UngVienDaXepHang> xepHang = mt.timThoService.xepHang(DIEM_KHACH, LOAI_THUNG_LOP, 5.0, MoiTruongTest.BAY_GIO);

        assertThat(xepHang).extracting(x -> x.ungVien().thoId()).containsExactly(2, 1);
        assertThat(xepHang.get(0).diem()).isGreaterThan(xepHang.get(1).diem());
    }
}
