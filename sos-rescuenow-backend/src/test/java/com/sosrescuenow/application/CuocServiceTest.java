package com.sosrescuenow.application;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.cuoc.ThoTrenCuoc;
import com.sosrescuenow.application.cuoc.GuiSosCommand;
import com.sosrescuenow.application.cuoc.KetQuaGuiSos;
import com.sosrescuenow.application.fake.MoiTruongTest;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.domain.model.VaiTro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sosrescuenow.application.fake.MoiTruongTest.DIEM_KHACH;
import static com.sosrescuenow.application.fake.MoiTruongTest.KHACH_AN;
import static com.sosrescuenow.application.fake.MoiTruongTest.LOAI_THUNG_LOP;
import static com.sosrescuenow.application.fake.MoiTruongTest.cachKhachKm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CuocServiceTest {

    private MoiTruongTest mt;

    @BeforeEach
    void setUp() {
        mt = new MoiTruongTest();
    }

    private GuiSosCommand lenh(int khachId) {
        return new GuiSosCommand(khachId, LOAI_THUNG_LOP, DIEM_KHACH.latitude(), DIEM_KHACH.longitude(),
                "  Ngã tư A  ", "   ");
    }

    // ---------- Gui SOS ----------

    @Test
    void guiSos_taoCuocDangTim_giaBanNgayVaGhiLichSu() {
        mt.themThoSanSang(1, cachKhachKm(1.0), 5, 1);
        mt.themThoSanSang(2, cachKhachKm(8.0), 5, 1); // ngoai 5 km

        KetQuaGuiSos kq = mt.cuocService.guiSos(lenh(KHACH_AN));

        CuocCuuHo cuoc = kq.cuoc();
        assertThat(cuoc.getId()).isNotNull();
        assertThat(cuoc.getTrangThai()).isEqualTo(TrangThaiCuoc.DANG_TIM);
        assertThat(cuoc.getGiaUocTinh()).isEqualByComparingTo("80000");
        assertThat(cuoc.getDiaChi()).isEqualTo("Ngã tư A");   // da trim
        assertThat(cuoc.getGhiChu()).isNull();                // chuoi trang -> null
        assertThat(kq.soThoPhuHop()).isEqualTo(1);
        assertThat(mt.lichSu.tatCa()).hasSize(1);
        assertThat(mt.lichSu.tatCa().get(0).tu()).isNull();
        assertThat(mt.lichSu.tatCa().get(0).den()).isEqualTo(TrangThaiCuoc.DANG_TIM);
    }

    @Test
    void guiSos_hai_lan_biTuChoiKhiDangCoCuoc() {
        mt.cuocService.guiSos(lenh(KHACH_AN));

        assertThatThrownBy(() -> mt.cuocService.guiSos(lenh(KHACH_AN))).isInstanceOf(XungDotException.class);
        assertThat(mt.cuocService.guiSos(lenh(KHACH_AN + 1)).cuoc().getId()).isNotNull(); // khach khac van duoc
    }

    @Test
    void guiSos_duocLaiSauKhiCuocKetThuc() {
        CuocCuuHo cuoc = mt.cuocService.guiSos(lenh(KHACH_AN)).cuoc();
        mt.cuocService.huyCuoc(cuoc.getId(), KHACH_AN, null);

        assertThat(mt.cuocService.guiSos(lenh(KHACH_AN)).cuoc().getId()).isNotEqualTo(cuoc.getId());
    }

    @Test
    void guiSos_toaDoSai_biTuChoi() {
        assertThatThrownBy(() -> mt.cuocService.guiSos(
                new GuiSosCommand(KHACH_AN, LOAI_THUNG_LOP, 100, 0, null, null)))
                .isInstanceOf(DuLieuKhongHopLeException.class);
    }

    @Test
    void guiSos_loaiSuCoKhongTonTai_biTuChoi() {
        assertThatThrownBy(() -> mt.cuocService.guiSos(
                new GuiSosCommand(KHACH_AN, 999, DIEM_KHACH.latitude(), DIEM_KHACH.longitude(), null, null)))
                .isInstanceOf(DuLieuKhongHopLeException.class);
    }

    // ---------- Huy cuoc ----------

    @Test
    void huyCuoc_khiDangTim_thanhCongVoiLyDoMacDinh() {
        CuocCuuHo cuoc = mt.cuocService.guiSos(lenh(KHACH_AN)).cuoc();

        CuocCuuHo sau = mt.cuocService.huyCuoc(cuoc.getId(), KHACH_AN, "  ");

        assertThat(sau.getTrangThai()).isEqualTo(TrangThaiCuoc.HUY);
        assertThat(sau.getLyDoHuy()).isEqualTo("Khách hủy");
    }

    @Test
    void huyCuoc_khiDaCoTho_traThoVeRanh() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());
        assertThat(mt.hoSoRepo.timHoSo(1).orElseThrow().dangBan()).isTrue();

        CuocCuuHo sau = mt.cuocService.huyCuoc(cuoc.getId(), KHACH_AN, "Đổi ý");

        assertThat(sau.getTrangThai()).isEqualTo(TrangThaiCuoc.HUY);
        assertThat(mt.hoSoRepo.timHoSo(1).orElseThrow().dangBan()).isFalse();
        assertThat(mt.lichSu.tatCa().get(mt.lichSu.tatCa().size() - 1).ghiChu()).isEqualTo("Đổi ý");
    }

    @Test
    void huyCuoc_khiDangSua_biTuChoi() {
        mt.themThoSanSang(1, DIEM_KHACH, 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_DEN);
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DA_DEN);
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_SUA);

        assertThatThrownBy(() -> mt.cuocService.huyCuoc(cuoc.getId(), KHACH_AN, null))
                .isInstanceOf(TrangThaiKhongHopLeException.class);
    }

    @Test
    void huyCuoc_cuaNguoiKhac_biTuChoi() {
        CuocCuuHo cuoc = mt.cuocService.guiSos(lenh(KHACH_AN)).cuoc();
        assertThatThrownBy(() -> mt.cuocService.huyCuoc(cuoc.getId(), KHACH_AN + 1, null))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void huyCuoc_khongTonTai() {
        assertThatThrownBy(() -> mt.cuocService.huyCuoc(12345L, KHACH_AN, null))
                .isInstanceOf(KhongTimThayException.class);
    }

    // ---------- Xem cuoc ----------

    @Test
    void xemCuoc_phanQuyen() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());
        Long id = cuoc.getId();

        assertThat(mt.cuocService.xemCuoc(id, new AuthenticatedUser(KHACH_AN, VaiTro.USER))).isNotNull();
        assertThat(mt.cuocService.xemCuoc(id, new AuthenticatedUser(1, VaiTro.THO))).isNotNull();
        assertThat(mt.cuocService.xemCuoc(id, new AuthenticatedUser(999, VaiTro.ADMIN))).isNotNull();

        assertThatThrownBy(() -> mt.cuocService.xemCuoc(id, new AuthenticatedUser(KHACH_AN + 1, VaiTro.USER)))
                .isInstanceOf(KhongCoQuyenException.class);
        assertThatThrownBy(() -> mt.cuocService.xemCuoc(id, new AuthenticatedUser(2, VaiTro.THO)))
                .isInstanceOf(KhongCoQuyenException.class);
        // tho co id trung voi id khach nhung vai tro khac khong duoc coi la chu cuoc
        assertThatThrownBy(() -> mt.cuocService.xemCuoc(id, new AuthenticatedUser(KHACH_AN, VaiTro.THO)))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    // ---------- Het han ----------

    @Test
    void xuLyCuocHetHan_chiDoiCuocQuaHan() {
        CuocCuuHo quaHan = mt.themCuocDangTim(KHACH_AN, 130);
        CuocCuuHo ranhGioi = mt.themCuocDangTim(KHACH_AN + 1, 120);
        CuocCuuHo conTrong = mt.themCuocDangTim(KHACH_AN + 2, 100);

        int soCuoc = mt.cuocService.xuLyCuocHetHan();

        assertThat(soCuoc).isEqualTo(2);
        assertThat(mt.trangThaiCuoc(quaHan.getId())).isEqualTo(TrangThaiCuoc.KHONG_CO_THO);
        assertThat(mt.trangThaiCuoc(ranhGioi.getId())).isEqualTo(TrangThaiCuoc.KHONG_CO_THO);
        assertThat(mt.trangThaiCuoc(conTrong.getId())).isEqualTo(TrangThaiCuoc.DANG_TIM);
        assertThat(mt.cuocService.xuLyCuocHetHan()).isZero(); // chay lai khong doi gi them
    }

    @Test
    void xuLyCuocHetHan_khongDongVaoCuocDaCoTho() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());

        assertThat(mt.cuocService.xuLyCuocHetHan()).isZero();
        assertThat(mt.trangThaiCuoc(cuoc.getId())).isEqualTo(TrangThaiCuoc.DA_NHAN);
    }

    // ---------- Theo doi tho tren ban do ----------

    @Test
    void thoCuaCuoc_truocKhiCoThoThiRong_sauKhiNhanThiCoTenVaViTri() {
        mt.nguoiDungRepo.them(1, "Thợ Dũng", VaiTro.THO);
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        assertThat(mt.cuocService.thoCuaCuoc(cuoc.getId(), KHACH_AN).isEmpty()).isTrue();

        mt.thoService.nhanCuoc(1, cuoc.getId());
        ThoTrenCuoc tho = mt.cuocService.thoCuaCuoc(cuoc.getId(), KHACH_AN).orElseThrow();

        assertThat(tho.thoId()).isEqualTo(1);
        assertThat(tho.hoTen()).isEqualTo("Thợ Dũng");
        assertThat(tho.viTri().latitude()).isEqualTo(cachKhachKm(0.5).latitude());
        assertThat(tho.capNhatLuc()).isEqualTo(MoiTruongTest.BAY_GIO);
    }

    @Test
    void thoCuaCuoc_nguoiKhacKhongDuocXem() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());

        assertThatThrownBy(() -> mt.cuocService.thoCuaCuoc(cuoc.getId(), KHACH_AN + 1))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void thoCuaCuoc_khiCuocDaHuyThiRong() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());
        mt.cuocService.huyCuoc(cuoc.getId(), KHACH_AN, null);

        assertThat(mt.cuocService.thoCuaCuoc(cuoc.getId(), KHACH_AN).isEmpty()).isTrue();
    }
}
