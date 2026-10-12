package com.sosrescuenow.application;

import com.sosrescuenow.application.fake.MoiTruongTest;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.HoSoTho;
import com.sosrescuenow.domain.model.ThoTomTat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.sosrescuenow.application.fake.MoiTruongTest.KHACH_AN;
import static com.sosrescuenow.application.fake.MoiTruongTest.LOAI_HET_XANG;
import static com.sosrescuenow.application.fake.MoiTruongTest.LOAI_THUNG_LOP;
import static com.sosrescuenow.application.fake.MoiTruongTest.cachKhachKm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminServiceTest {

    private MoiTruongTest mt;

    @BeforeEach
    void setUp() {
        mt = new MoiTruongTest();
        mt.hoSoRepo.taoHoSoMoi(10);   // tho moi dang ky: chua duyet, khong co loai su co
        mt.hoSoRepo.taoHoSoMoi(11);
        mt.hoSoRepo.them(12, true, false, true, 4.5, 0.9, LOAI_THUNG_LOP); // tho da duyet
    }

    private HoSoTho hoSo(int id) {
        return mt.hoSoRepo.timHoSo(id).orElseThrow();
    }

    @Test
    void danhSachTho_locChiChuaDuyet() {
        List<ThoTomTat> chuaDuyet = mt.adminService.danhSachTho(true);
        List<ThoTomTat> tatCa = mt.adminService.danhSachTho(false);

        assertThat(chuaDuyet).extracting(ThoTomTat::id).containsExactly(11, 10);
        assertThat(tatCa).extracting(ThoTomTat::id).containsExactly(12, 11, 10);
        assertThat(tatCa.get(0).diemTrungBinh()).isEqualTo(4.5);
    }

    @Test
    void duyetTho_khongChonLoai_choPhepTatCa() {
        mt.adminService.duyetTho(10, null);

        HoSoTho h = hoSo(10);
        assertThat(h.daDuyet()).isTrue();
        assertThat(h.loaiSuCoLamDuoc()).isEqualTo(Set.of(LOAI_THUNG_LOP, LOAI_HET_XANG));
        assertThat(mt.adminService.danhSachTho(true)).extracting(ThoTomTat::id).containsExactly(11);
    }

    @Test
    void duyetTho_chonLoaiCuThe_chiLamLoaiDo() {
        mt.adminService.duyetTho(10, Set.of(LOAI_HET_XANG));
        assertThat(hoSo(10).loaiSuCoLamDuoc()).isEqualTo(Set.of(LOAI_HET_XANG));
    }

    @Test
    void duyetTho_loaiKhongTonTai_biTuChoiVaKhongDuyet() {
        assertThatThrownBy(() -> mt.adminService.duyetTho(10, Set.of(LOAI_THUNG_LOP, 999)))
                .isInstanceOf(DuLieuKhongHopLeException.class);
        assertThat(hoSo(10).daDuyet()).isFalse();
    }

    @Test
    void duyetTho_khongTonTai() {
        assertThatThrownBy(() -> mt.adminService.duyetTho(404, null)).isInstanceOf(KhongTimThayException.class);
    }

    @Test
    void duyetLai_thoDaDuyet_chiDoiLoaiSuCo() {
        mt.adminService.duyetTho(12, Set.of(LOAI_HET_XANG));
        assertThat(hoSo(12).daDuyet()).isTrue();
        assertThat(hoSo(12).loaiSuCoLamDuoc()).isEqualTo(Set.of(LOAI_HET_XANG));
    }

    @Test
    void truocKhiDuyetKhongBatOnlineDuoc_sauKhiDuyetNhanDuocCuoc() {
        mt.viTriRepo.luu(10, cachKhachKm(0.5), MoiTruongTest.BAY_GIO);
        assertThatThrownBy(() -> mt.thoService.datTrangThaiOnline(10, true))
                .isInstanceOf(KhongCoQuyenException.class);

        mt.adminService.duyetTho(10, null);
        mt.thoService.datTrangThaiOnline(10, true);

        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        assertThat(mt.thoService.nhanCuoc(10, cuoc.getId()).getThoId()).isEqualTo(10);
    }
}
